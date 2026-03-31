package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime reminderDateTime;

    @Builder.Default
    private boolean isProcessed = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}