package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.VehicleDTO;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Entity.Vehicle;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import edu.lk.ijse.smartlife_backend.Repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public String saveVehicle(VehicleDTO vehicleDTO) {
        User admin = userRepository.findById(vehicleDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + vehicleDTO.getAdminId()));

        String lastId = vehicleRepository.findLastVehicleId();
        String nextId;
        if (lastId == null) {
            nextId = "VEH-00001";
        } else {
            int id = Integer.parseInt(lastId.split("-")[1]);
            nextId = String.format("VEH-%05d", id + 1);
        }

        Vehicle vehicle = Vehicle.builder()
                .vehi_id(nextId)
                .vehi_number(vehicleDTO.getVehi_number())
                .admin(admin)
                .fenceLat(vehicleDTO.getFenceLat())
                .fenceLng(vehicleDTO.getFenceLng())
                .fenceRadius(vehicleDTO.getFenceRadius())
                .status("SAFE")
                .build();

        vehicleRepository.save(vehicle);
        return "Vehicle register successful with ID: " + nextId;
    }

    public String updateVehicle(VehicleDTO vehicleDTO) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicleDTO.getVehi_id())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleDTO.getVehi_id()));

        existingVehicle.setVehi_number(vehicleDTO.getVehi_number());
        existingVehicle.setFenceLat(vehicleDTO.getFenceLat());
        existingVehicle.setFenceLng(vehicleDTO.getFenceLng());
        existingVehicle.setFenceRadius(vehicleDTO.getFenceRadius());

        vehicleRepository.save(existingVehicle);
        return "Vehicle updated successfully!";
    }

    @Transactional
    public String deleteVehicle(String vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new RuntimeException("Vehicle not found!");
        }

        vehicleRepository.deleteById(vehicleId);
        return "Vehicle deleted successfully!";
    }

    @Transactional
    public String updateVehicleLocation(String vehicleId, Double lat, Double lng) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        vehicle.setLastLat(lat);
        vehicle.setLastLng(lng);

        if (vehicle.getFenceLat() != null && vehicle.getFenceRadius() != null) {
            double distance = calculateDistance(lat, lng, vehicle.getFenceLat(), vehicle.getFenceLng());

            if (distance > vehicle.getFenceRadius()) {
                vehicle.setStatus("OUT_OF_ZONE");
            } else {
                vehicle.setStatus("SAFE");
            }
        }

        vehicleRepository.save(vehicle);
        return "Location updated. Status: " + vehicle.getStatus();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    public List<Vehicle> getAllVehiclesByAdmin(String adminId) {
        return vehicleRepository.findAllByAdmin_UserId(adminId);
    }
}