package edu.lk.ijse.smartlife_backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class Member {
    @Id
    private String memberId;

    private String fullName;
    private String nickname;
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_tracking_enabled", columnDefinition = "BIT")
    private boolean isTrackingEnabled;
    private Double lastLat;
    private Double lastLng;
    private LocalDateTime lastLocationUpdate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User admin;


}