package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.ReminderDTO;
import edu.lk.ijse.smartlife_backend.Entity.Reminder;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.MemberRepository;
import edu.lk.ijse.smartlife_backend.Repository.ReminderRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepository.findByEmail(email);
        if(user.isPresent()) return user.get();

        var member = memberRepository.findByEmail(email);
        if(member.isPresent()) {
            return member.get().getAdmin();
        }

        throw new RuntimeException("User not found");
    }
    public void saveReminder(String title, LocalDateTime dateTime) {
        User currentUser = getCurrentUser();
        Reminder reminder = Reminder.builder()
                .title(title)
                .reminderDateTime(dateTime)
                .isProcessed(false)
                .user(currentUser)
                .build();
        reminderRepository.save(reminder);
    }

    public List<ReminderDTO> getMyPendingReminders() {
        User currentUser = getCurrentUser();
        List<Reminder> reminders = reminderRepository.findAllByUserAndIsProcessedFalseOrderByReminderDateTimeAsc(currentUser);

        return reminders.stream().map(r -> ReminderDTO.builder()
                .reminderId(r.getReminderId())
                .title(r.getTitle())
                .reminderDateTime(r.getReminderDateTime())
                .isProcessed(r.isProcessed())
                .build()
        ).toList();
    }

    @Transactional
    public void markAsProcessed(Long id) {
        User currentUser = getCurrentUser();
        reminderRepository.findById(id).ifPresent(r -> {
            if (r.getUser().getUserId().equals(currentUser.getUserId())) {
                r.setProcessed(true);
                reminderRepository.save(r);
            }
        });
    }
}