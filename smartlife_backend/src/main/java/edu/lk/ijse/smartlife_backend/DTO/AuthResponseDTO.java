package edu.lk.ijse.smartlife_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String access_token;
    private String role;
    private String userId;
    private String adminId;
}
