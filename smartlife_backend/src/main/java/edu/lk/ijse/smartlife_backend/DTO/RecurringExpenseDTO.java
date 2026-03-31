package edu.lk.ijse.smartlife_backend.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurringExpenseDTO {

    private Long expenseId;

    @NotBlank(message = "Expense title is required")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String expenseTitle;

    @NotNull(message = "Monthly amount is required")
    @Min(value = 1, message = "Amount must be at least 1.00")
    private Double monthlyAmount;

    @FutureOrPresent(message = "Expiry date cannot be in the past")
    private LocalDate expiryDate;

    private Boolean isActive;
}