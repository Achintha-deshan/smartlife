package edu.lk.ijse.smartlife_backend.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBudgetDTO {

    @NotBlank(message = "Budget month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Invalid month format. Use YYYY-MM")
    private String budgetMonth;

    @NotNull(message = "Estimated salary is required")
    @Min(value = 0, message = "Salary cannot be negative")
    private Double estimatedSalary;

    @NotNull(message = "Monthly savings goal is required")
    @Min(value = 0, message = "Savings goal cannot be negative")
    private Double monthlySavingsGoal;

    @Min(value = 0, message = "Actual salary cannot be negative")
    private Double actualSalary;

    private Boolean isFinalized;
    private Boolean isRecurring;
}