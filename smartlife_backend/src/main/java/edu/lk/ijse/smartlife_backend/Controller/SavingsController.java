package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.SavingsAccountDTO;
import edu.lk.ijse.smartlife_backend.DTO.SavingsTransactionDTO;
import edu.lk.ijse.smartlife_backend.Service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
@CrossOrigin
public class SavingsController {

    private final SavingsService savingsService;

    @GetMapping("/balance")
    public ResponseEntity<SavingsAccountDTO> getSavingsBalance() {
        return ResponseEntity.ok(savingsService.getMySavingsAccountDTO());
    }

    @GetMapping("/history")
    public ResponseEntity<List<SavingsTransactionDTO>> getTransactionHistory() {
        return ResponseEntity.ok(savingsService.getTransactionHistory());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawFromSavings(@RequestParam double amount) {
        try {
            savingsService.withdrawFromSavings(amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> manualDeposit(@RequestParam double amount, @RequestParam String month) {
        savingsService.addToSavings(amount, "MANUAL_DEPOSIT", month);
        return ResponseEntity.ok("Deposit successful!");
    }
}