package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle,String> {

    List<Vehicle> findAllByAdmin_UserId(String adminId);

    @Query(value = "SELECT vehi_id FROM vehicle ORDER BY vehi_id DESC LIMIT 1", nativeQuery = true)
    String findLastVehicleId();
}
