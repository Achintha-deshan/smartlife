package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String userId;
    @Column(unique = true)
    private String email;
    private String fullName;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
