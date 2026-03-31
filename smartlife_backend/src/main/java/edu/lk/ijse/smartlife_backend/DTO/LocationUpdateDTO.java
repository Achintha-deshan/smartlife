package edu.lk.ijse.smartlife_backend.DTO;

import lombok.Data;

@Data
public class LocationUpdateDTO {
    private String memberId;
    private Double latitude;
    private Double longitude;
}
