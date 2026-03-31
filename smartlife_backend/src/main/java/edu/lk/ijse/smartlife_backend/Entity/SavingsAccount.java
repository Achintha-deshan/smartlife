package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savingsId;

    @Column(nullable = false)
    private Double totalBalance = 0.0;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}