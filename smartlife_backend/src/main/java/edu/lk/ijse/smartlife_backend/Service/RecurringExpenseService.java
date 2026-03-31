package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.RecurringExpenseDTO;
import edu.lk.ijse.smartlife_backend.Entity.RecurringExpense;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.RecurringExpenseRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurringExpenseService {

    private final RecurringExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public String saveExpenseFromDTO(RecurringExpenseDTO dto) {
        User currentUser = getCurrentUser();

        RecurringExpense expense = RecurringExpense.builder()
                .expenseTitle(dto.getExpenseTitle())
                .monthlyAmount(dto.getMonthlyAmount())
                .expiryDate(dto.getExpiryDate())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .user(currentUser)
                .build();

        expenseRepository.save(expense);
        return "Fixed expense '" + dto.getExpenseTitle() + "' added successfully!";
    }

    public List<RecurringExpense> getAllActiveExpenses() {
        User currentUser = getCurrentUser();
        return expenseRepository.findAllByIsActiveTrueAndUser(currentUser);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with ID: " + id);
        }
        expenseRepository.deleteById(id);
    }

//    public void deactivateExpense(Long id) {
//        expenseRepository.findById(id).ifPresent(expense -> {
//            expense.setIsActive(false);
//            expenseRepository.save(expense);
//        });
//    }
}