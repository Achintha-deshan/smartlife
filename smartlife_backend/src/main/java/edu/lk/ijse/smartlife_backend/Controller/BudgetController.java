package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.APIResponse;
import edu.lk.ijse.smartlife_backend.DTO.MonthlyBudgetDTO;
import edu.lk.ijse.smartlife_backend.DTO.RecurringExpenseDTO;
import edu.lk.ijse.smartlife_backend.Entity.MonthlyBudget;
import edu.lk.ijse.smartlife_backend.Entity.RecurringExpense;
import edu.lk.ijse.smartlife_backend.Service.BudgetService;
import edu.lk.ijse.smartlife_backend.Service.RecurringExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@CrossOrigin
public class BudgetController {

    private final BudgetService budgetService;
    private final RecurringExpenseService expenseService;

    @PostMapping("/budget")
    public ResponseEntity<APIResponse<String>> saveBudget(@Valid @RequestBody MonthlyBudgetDTO dto) {
        String resultMessage = budgetService.saveOrUpdateBudgetFromDTO(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new APIResponse<>(200, resultMessage, "Success"));
    }

    @GetMapping("/daily-limit/{month}")
    public ResponseEntity<APIResponse<Double>> getDailyLimit(@PathVariable String month) {
        double limit = budgetService.getCalculatedDailyLimit(month);
        return ResponseEntity.ok(new APIResponse<>(200, "Daily limit calculated", limit));
    }

    @PostMapping("/expense")
    public ResponseEntity<APIResponse<String>> addExpense(@Valid @RequestBody RecurringExpenseDTO dto) {
        expenseService.saveExpenseFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Fixed expense added!", "Success"));
    }

    @GetMapping("/expenses")
    public ResponseEntity<APIResponse<List<RecurringExpense>>> getAllExpenses() {
        List<RecurringExpense> expenses = expenseService.getAllActiveExpenses();
        return ResponseEntity.ok(new APIResponse<>(200, "Fetched active expenses", expenses));
    }

    @DeleteMapping("/expense/{id}")
    public ResponseEntity<APIResponse<String>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Expense deleted!", "Deleted ID: " + id));
    }

    @GetMapping("/budget/{month}")
    public ResponseEntity<APIResponse<MonthlyBudget>> getBudgetByMonth(@PathVariable String month) {
        return budgetService.getBudgetByMonth(month)
                .map(budget -> ResponseEntity.ok(new APIResponse<>(200, "Budget found", budget)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIResponse<>(404, "No budget found for this month", null)));
    }
}