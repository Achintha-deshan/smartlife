package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.MonthlyBudgetDTO;
import edu.lk.ijse.smartlife_backend.Entity.*;
import edu.lk.ijse.smartlife_backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final MonthlyBudgetRepository budgetRepository;
    private final RecurringExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final DailyExpenseRepository dailyExpenseRepository;
    private final MemberRepository memberRepository;
    private final SavingsService savingsService;

    private User getTargetAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) return user.get();

        return memberRepository.findByEmail(email)
                .map(Member::getAdmin)
                .orElseThrow(() -> new UsernameNotFoundException("User or Member not found with email: " + email));
    }

    @Transactional
    public String saveOrUpdateBudgetFromDTO(MonthlyBudgetDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("This action is only allowed for Admin."));

        Optional<MonthlyBudget> existingBudgetOpt = budgetRepository.findByBudgetMonthAndUser(dto.getBudgetMonth(), currentUser);
        boolean isNewBudget = existingBudgetOpt.isEmpty();
        MonthlyBudget budget;

        if (!isNewBudget) {
            budget = existingBudgetOpt.get();
            if (Boolean.TRUE.equals(budget.getIsFinalized())) {
                return "This month's budget is already finalized and cannot be modified.";
            }
            budget.setEstimatedSalary(dto.getEstimatedSalary());
            budget.setMonthlySavingsGoal(dto.getMonthlySavingsGoal());
            budget.setActualSalary(dto.getActualSalary());
            budget.setIsRecurring(dto.getIsRecurring() != null && dto.getIsRecurring());
        } else {
            budget = MonthlyBudget.builder()
                    .budgetMonth(dto.getBudgetMonth())
                    .estimatedSalary(dto.getEstimatedSalary())
                    .monthlySavingsGoal(dto.getMonthlySavingsGoal())
                    .actualSalary(dto.getActualSalary())
                    .isFinalized(false)
                    .isRecurring(dto.getIsRecurring() != null && dto.getIsRecurring())
                    .user(currentUser)
                    .build();
        }

        budgetRepository.save(budget);


        if (isNewBudget && budget.getMonthlySavingsGoal() > 0) {
            savingsService.addToSavings(budget.getMonthlySavingsGoal(), "DEPOSIT_GOAL", budget.getBudgetMonth());
        }

        if (Boolean.TRUE.equals(dto.getIsFinalized())) {
            finalizeAndTransferToSavings(dto.getBudgetMonth());
            return "Budget finalized and remaining funds transferred to savings for " + dto.getBudgetMonth();
        }

        return "Budget saved successfully. Monthly goal added to savings.";
    }

    public double getCalculatedDailyLimit(String month) {
        User targetAdmin = getTargetAdmin();
        Optional<MonthlyBudget> budgetOpt = budgetRepository.findByBudgetMonthAndUser(month, targetAdmin);
        if (budgetOpt.isEmpty()) return 0.0;

        MonthlyBudget budget = budgetOpt.get();
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate today = LocalDate.now();

        double totalFixedExpenses = expenseRepository.findAllByIsActiveTrueAndUser(targetAdmin).stream()
                .mapToDouble(RecurringExpense::getMonthlyAmount)
                .sum();

        double initialDisposableIncome = budget.getEstimatedSalary() - (budget.getMonthlySavingsGoal() + totalFixedExpenses);

        List<DailyExpense> allExpenses = dailyExpenseRepository.findAllByAdminIdAndMonth(targetAdmin.getUserId(), month);

        double spentBeforeToday = allExpenses.stream()
                .filter(exp -> exp.getDate().isBefore(today))
                .mapToDouble(DailyExpense::getAmount)
                .sum();

        double capitalAtStartOfToday = initialDisposableIncome - spentBeforeToday;

        int remainingDays = (yearMonth.lengthOfMonth() - today.getDayOfMonth()) + 1;

        if (remainingDays <= 0) return 0.0;

        double dailyTarget = capitalAtStartOfToday / remainingDays;

        double spentToday = allExpenses.stream()
                .filter(exp -> exp.getDate().isEqual(today))
                .mapToDouble(DailyExpense::getAmount)
                .sum();

        return Math.max(dailyTarget - spentToday, 0.0);
    }
    @Transactional
    public void finalizeAndTransferToSavings(String month) {
        User targetAdmin = getTargetAdmin();
        MonthlyBudget budget = budgetRepository.findByBudgetMonthAndUser(month, targetAdmin)
                .orElseThrow(() -> new RuntimeException("Budget not found!"));

        if (Boolean.TRUE.equals(budget.getIsFinalized())) return;

        double totalFixedExpenses = expenseRepository.findAllByIsActiveTrueAndUser(targetAdmin).stream()
                .mapToDouble(RecurringExpense::getMonthlyAmount).sum();

        double initialDisposableIncome = budget.getEstimatedSalary() - (budget.getMonthlySavingsGoal() + totalFixedExpenses);

        double totalSpentInMonth = dailyExpenseRepository.findAllByAdminIdAndMonth(targetAdmin.getUserId(), month).stream()
                .mapToDouble(DailyExpense::getAmount).sum();

        double remainingBalance = Math.max(initialDisposableIncome - totalSpentInMonth, 0.0);

        if (remainingBalance > 0) {
            savingsService.addToSavings(remainingBalance, "DEPOSIT_REMAINING", month);
        }

        budget.setIsFinalized(true);
        budgetRepository.save(budget);
    }

    public Optional<MonthlyBudget> getBudgetByMonth(String month) {
        return budgetRepository.findByBudgetMonthAndUser(month, getTargetAdmin());
    }
}