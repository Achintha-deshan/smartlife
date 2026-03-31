package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.SavingsAccount;
import edu.lk.ijse.smartlife_backend.Entity.SavingsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<SavingsTransaction, Long> {
    List<SavingsTransaction> findAllBySavingsAccountOrderByDateTimeDesc(SavingsAccount account);
}
