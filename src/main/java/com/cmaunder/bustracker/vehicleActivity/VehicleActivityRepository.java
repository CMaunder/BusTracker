package com.cmaunder.bustracker.vehicleActivity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleActivityRepository extends JpaRepository<VehicleActivity, Long> {

//    @Query("SELECT a FROM vehicle_activity WHERE")
    List<VehicleActivity> findAllByVehicleRefOrderByRecordedAtTimeDesc(String vehicleRef);

    List<VehicleActivity> findAllByPublishedLineNameOrderByRecordedAtTimeDesc(String publishedLineName);

    Optional<VehicleActivity> findFirstBy();

}


//"2024-03-23T17:11:15Z",