package edu.lk.ijse.smartlife_backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Vehicle {
    @Id
    private String vehi_id;
    private String vehi_number;

    private Double lastLat;
    private Double lastLng;

    private Double fenceLat;
    private Double fenceLng;
    private Double fenceRadius;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User admin;
}
