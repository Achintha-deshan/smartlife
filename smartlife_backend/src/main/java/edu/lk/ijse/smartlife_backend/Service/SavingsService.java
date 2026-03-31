package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.SavingsAccountDTO;
import edu.lk.ijse.smartlife_backend.DTO.SavingsTransactionDTO;
import edu.lk.ijse.smartlife_backend.Entity.SavingsAccount;
import edu.lk.ijse.smartlife_backend.Entity.SavingsTransaction;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.SavingsAccountRepository;
import edu.lk.ijse.smartlife_backend.Repository.TransactionRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional

public class SavingsService {

    private final SavingsAccountRepository savingsRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("Authentication failed!");
        }

        String username = auth.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User record not found in database: " + username));
    }

    public void addToSavings(double amount, String type, String month) {
        if (amount <= 0) return;

        User currentUser = getCurrentUser();
        SavingsAccount account = getOrCreateAccount(currentUser);

        account.setTotalBalance(account.getTotalBalance() + amount);
        account.setLastUpdated(LocalDateTime.now());
        savingsRepository.save(account);

        transactionRepository.save(SavingsTransaction.builder()
                .amount(amount)
                .transactionType(type)
                .referenceMonth(month)
                .dateTime(LocalDateTime.now())
                .savingsAccount(account)
                .build());

    }

    public void withdrawFromSavings(double amount) {
        if (amount <= 0) throw new RuntimeException("Withdraw amount must be greater than zero");

        User currentUser = getCurrentUser();
        SavingsAccount account = savingsRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("No savings account found!"));

        if (account.getTotalBalance() < amount) {
            throw new RuntimeException("Insufficient balance to withdraw!");
        }

        account.setTotalBalance(account.getTotalBalance() - amount);
        account.setLastUpdated(LocalDateTime.now());
        savingsRepository.save(account);

        transactionRepository.save(SavingsTransaction.builder()
                .amount(amount)
                .transactionType("WITHDRAWAL")
                .dateTime(LocalDateTime.now())
                .savingsAccount(account)
                .build());
    }

    public SavingsAccountDTO getMySavingsAccountDTO() {
        User currentUser = getCurrentUser();
        SavingsAccount account = getOrCreateAccount(currentUser);

        return SavingsAccountDTO.builder()
                .savingsId(account.getSavingsId())
                .totalBalance(account.getTotalBalance())
                .lastUpdated(account.getLastUpdated())
                .userEmail(currentUser.getEmail())
                .build();
    }

    private SavingsAccount getOrCreateAccount(User user) {
        return savingsRepository.findByUser(user)
                .orElseGet(() -> savingsRepository.save(SavingsAccount.builder()
                        .user(user)
                        .totalBalance(0.0)
                        .lastUpdated(LocalDateTime.now())
                        .build()));
    }

    public List<SavingsTransactionDTO> getTransactionHistory() {
        User currentUser = getCurrentUser();
        SavingsAccount account = getOrCreateAccount(currentUser);

        return transactionRepository.findAllBySavingsAccountOrderByDateTimeDesc(account)
                .stream()
                .map(tx -> SavingsTransactionDTO.builder()
                        .transactionId(tx.getTransactionId())
                        .amount(tx.getAmount())
                        .transactionType(tx.getTransactionType())
                        .referenceMonth(tx.getReferenceMonth())
                        .dateTime(tx.getDateTime())
                        .build())
                .collect(Collectors.toList());
    }

    public double getTotalSavings() {
        try {
            User currentUser = getCurrentUser();
            return savingsRepository.findByUser(currentUser)
                    .map(SavingsAccount::getTotalBalance)
                    .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
}