package com.cmaunder.bustracker.vehicleActivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URISyntaxException;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
public class VehicleActivityScheduler {

    @Autowired
    private VehicleActivityService vehicleActivityService;

    // Scheduled every 1 minute - 1M
    @Scheduled(initialDelayString = "PT1S", fixedRateString = "PT1M")
    void scheduledVehicleActivityUpdate() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Calling fetchAndSaveActivityData");
        int number_new_records_saved = vehicleActivityService.fetchAndSaveActivityData(10);
        System.out.println("Saved " + number_new_records_saved + " new vehicle activity records.");
    }
}
