package com.cmaunder.bustracker.vehicleActivity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleActivityRepository extends JpaRepository<VehicleActivity, Long> {

//    @Query("SELECT a FROM vehicle_activity WHERE")
    List<VehicleActivity> findAllByVehicleRefOrderByRecordedAtTimeDesc(String vehicleRef);

    List<VehicleActivity> findAllByRecordedAtTimeAfterOrderByRecordedAtTimeDesc(LocalDateTime since);

    @Query("SELECT a from VehicleActivity a " +
            "WHERE a.vehicleRef = ?1 AND a.recordedAtTime >= ?2 " +
            "ORDER BY a.recordedAtTime DESC")
    List<VehicleActivity> findAllActivitiesByVehicleRefSince(String vehicleRef, LocalDateTime sinceDateTime);

    @Query("SELECT a from VehicleActivity a " +
            "WHERE a.publishedLineName = ?1 AND a.recordedAtTime >= ?2 " +
            "ORDER BY a.recordedAtTime DESC")
    List<VehicleActivity> findAllActivitiesByRouteSince(String publishedLineName, LocalDateTime sinceDateTime);

    List<VehicleActivity> findAllByPublishedLineNameOrderByRecordedAtTimeDesc(String publishedLineName);

    Optional<VehicleActivity> findFirstBy();

}
