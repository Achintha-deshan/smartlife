package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.APIResponse;
import edu.lk.ijse.smartlife_backend.DTO.VehicleDTO;
import edu.lk.ijse.smartlife_backend.Entity.Vehicle;
import edu.lk.ijse.smartlife_backend.Service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/save")
    public ResponseEntity<APIResponse<String>> save(@Valid @RequestBody VehicleDTO vehicleDTO) {
        String message = vehicleService.saveVehicle(vehicleDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Vehicle saved successfully", message));
    }

    @GetMapping("/all/{adminId}")
    public ResponseEntity<APIResponse<List<Vehicle>>> getAllVehicles(@PathVariable String adminId) {
        List<Vehicle> vehicles = vehicleService.getAllVehiclesByAdmin(adminId);
        return ResponseEntity.ok(new APIResponse<>(200, "Vehicles retrieved successfully", vehicles));
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> update(@Valid @RequestBody VehicleDTO vehicleDTO) {
        String message = vehicleService.updateVehicle(vehicleDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "Vehicle updated successfully", message));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse<String>> delete(@PathVariable String id) {
        String message = vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Vehicle deleted successfully", message));
    }

    @PostMapping("/update-location")
    public ResponseEntity<APIResponse<String>> updateLocation(@RequestBody VehicleDTO vehicleDTO) {
        String result = vehicleService.updateVehicleLocation(
                vehicleDTO.getVehi_id(),
                vehicleDTO.getLastLat(),
                vehicleDTO.getLastLng()
        );

        return ResponseEntity.ok(new APIResponse<>(200, "Location Updated Successfully", result));
    }
}