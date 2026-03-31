package edu.lk.ijse.smartlife_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceSummaryDTO {
    private Double dailyLimit;
    private Double totalSavings;
    private Double totalFixedExpenses;
    private Double monthlySavingsGoal;
    private Double remainingBudget;
    private String status;
}