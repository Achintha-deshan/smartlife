package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.ReminderDTO;
import edu.lk.ijse.smartlife_backend.Service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@CrossOrigin
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/add")
    public ResponseEntity<String> addReminder(@RequestBody ReminderRequest request) {
        reminderService.saveReminder(request.getTitle(), request.getDateTime());
        return ResponseEntity.ok("Reminder saved successfully!");
    }

    @GetMapping("/my-pending")
    public ResponseEntity<List<ReminderDTO>> getMyReminders() {
        return ResponseEntity.ok(reminderService.getMyPendingReminders());
    }

    @PatchMapping("/process/{id}")
    public ResponseEntity<Void> processReminder(@PathVariable Long id) {
        reminderService.markAsProcessed(id);
        return ResponseEntity.ok().build();
    }

    @lombok.Data
    public static class ReminderRequest {
        private String title;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dateTime;
    }
}