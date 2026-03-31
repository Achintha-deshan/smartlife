package edu.lk.ijse.smartlife_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsTransactionDTO {
    private Long transactionId;
    private Double amount;
    private String transactionType;
    private String referenceMonth;
    private LocalDateTime dateTime;
}