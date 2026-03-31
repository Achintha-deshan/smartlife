package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MonthlyBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @Column(nullable = false, unique = true)
    private String budgetMonth;

    @Column(nullable = false)
    private Double estimatedSalary;

    @Column(nullable = true)
    private Double actualSalary;

    @Column(nullable = false)
    private Double monthlySavingsGoal = 0.0;

    @Column(nullable = false)
    private Boolean isFinalized = false;

    @Column(nullable = false)
    private Boolean isRecurring = true;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
