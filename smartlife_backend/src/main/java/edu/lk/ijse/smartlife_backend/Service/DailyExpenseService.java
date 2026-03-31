package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.DailyExpenseDTO;
import edu.lk.ijse.smartlife_backend.Entity.DailyExpense;
import edu.lk.ijse.smartlife_backend.Entity.Member;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.DailyExpenseRepository;
import edu.lk.ijse.smartlife_backend.Repository.MemberRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyExpenseService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final BudgetService budgetService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;



    public String saveDailyExpense(DailyExpenseDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User targetAdmin;
        Member memberWhoRecorded = null;

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            targetAdmin = userOpt.get();
        } else {
            memberWhoRecorded = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            targetAdmin = memberWhoRecorded.getAdmin();
        }

        DailyExpense expense = DailyExpense.builder()
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .date(dto.getDate() != null ? dto.getDate() : LocalDate.now())
                .user(targetAdmin)
                .recordedBy(memberWhoRecorded)
                .build();

        dailyExpenseRepository.save(expense);
        return "Expense recorded and deducted from " + targetAdmin.getFullName() + "'s budget.";
    }
    public List<DailyExpense> getAllExpensesForCurrentMonth() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String currentMonth = java.time.YearMonth.now().toString();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return dailyExpenseRepository.findAllByAdminIdAndMonth(user.get().getUserId(), currentMonth);
        }

        return memberRepository.findByEmail(email)
                .map(member -> dailyExpenseRepository.findAllByAdminIdAndMonth(member.getAdmin().getUserId(), currentMonth))
                .orElseThrow(() -> new RuntimeException("Data access denied for this role"));
    }

    @Transactional
    public String deleteExpense(Long id) {
        if (!dailyExpenseRepository.existsById(id)) {
            throw new RuntimeException("Expense record not found!");
        }
        dailyExpenseRepository.deleteById(id);
        return "Expense deleted successfully!";
    }

}
