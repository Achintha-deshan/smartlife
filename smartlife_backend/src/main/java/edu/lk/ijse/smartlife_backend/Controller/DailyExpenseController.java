package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.APIResponse;
import edu.lk.ijse.smartlife_backend.DTO.DailyExpenseDTO;
import edu.lk.ijse.smartlife_backend.Entity.DailyExpense;
import edu.lk.ijse.smartlife_backend.Service.DailyExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@CrossOrigin
@RequiredArgsConstructor
public class DailyExpenseController {
    private final DailyExpenseService dailyExpenseService;

    @PostMapping("/save")
    public ResponseEntity<APIResponse<String>> save(@Valid @RequestBody DailyExpenseDTO dto) {
        String message = dailyExpenseService.saveDailyExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Expense recorded", message));
    }

    @GetMapping("/current-month")
    public ResponseEntity<APIResponse<List<DailyExpense>>> getCurrentMonthExpenses() {
        List<DailyExpense> expenses = dailyExpenseService.getAllExpensesForCurrentMonth();
        return ResponseEntity.ok(new APIResponse<>(200, "Success", expenses));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse<String>> deleteExpense(@PathVariable Long id) {
        String message = dailyExpenseService.deleteExpense(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Deleted Successfully", message));
    }
}
