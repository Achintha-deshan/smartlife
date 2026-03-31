package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.SavingsAccount;
import edu.lk.ijse.smartlife_backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findByUser(User user);
}
