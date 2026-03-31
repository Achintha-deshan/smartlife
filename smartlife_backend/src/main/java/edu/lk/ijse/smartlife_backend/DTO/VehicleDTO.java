package edu.lk.ijse.smartlife_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTO {

    private String vehi_id;

    @NotBlank(message = "Vehicle number is required")
    @Size(min = 4, max = 15, message = "Vehicle number must be between 4 and 15 characters")
    @Pattern(regexp = "^[A-Z0-9 -]+$", message = "Vehicle number can only contain uppercase letters, numbers, spaces, and hyphens")
    private String vehi_number;

    @NotBlank(message = "Admin ID is required")
    private String adminId;

    private Double lastLat;
    private Double lastLng;

    private Double fenceLat;
    private Double fenceLng;
    private Double fenceRadius;

    private String status;
}