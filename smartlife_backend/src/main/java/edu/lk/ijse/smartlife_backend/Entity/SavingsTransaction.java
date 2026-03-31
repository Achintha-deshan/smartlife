package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Double amount;
    private String transactionType;
    private String referenceMonth;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "savings_id")
    private SavingsAccount savingsAccount;
}
