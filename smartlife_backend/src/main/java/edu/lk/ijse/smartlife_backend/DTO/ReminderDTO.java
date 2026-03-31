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
public class ReminderDTO {
    private Long reminderId;
    private String title;
    private LocalDateTime reminderDateTime;
    private boolean isProcessed;
}
