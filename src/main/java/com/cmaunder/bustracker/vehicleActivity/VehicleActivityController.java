package com.cmaunder.bustracker.vehicleActivity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path="api/v1/activity")
public class VehicleActivityController {

    private final VehicleActivityService vehicleActivityService;

    @Autowired
    public VehicleActivityController(VehicleActivityService vehicleActivityService) {
        this.vehicleActivityService = vehicleActivityService;
    }

    @GetMapping(path = "update-records")
    public String updateRecords(){
        int numberOfRecordsSaved;
        try {
            numberOfRecordsSaved = vehicleActivityService.fetchAndSaveActivityData();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return "Failed to update the data with the following reason: " + e;
        }
        return "Successfully wrote " + numberOfRecordsSaved + " new records to database.";
    }

    @GetMapping
    public List<VehicleActivity> getActivities(@RequestParam(required = false) String direction,
                                               @RequestParam(required = false) String since,
                                               @RequestParam(required = false) String vehicle,
                                               @RequestParam(required = false) String route) {

        return vehicleActivityService.getActivities(direction, since, vehicle, route);
    }

    @GetMapping(path = "all")
    public List<VehicleActivity> getAllActivities() {
        return vehicleActivityService.getAllActivities();
    }

    @GetMapping(path = "vehicle/{vehicleRef}")
    public List<VehicleActivity> getActivitiesByVehicleRef(@PathVariable("vehicleRef") String vehicleRef,
                                                           @RequestParam(required = false) String direction,
                                                           @RequestParam(required = false) String since) {
        if (since != null) {
            return vehicleActivityService.getActivitiesByVehicleRef(vehicleRef, direction, LocalDateTime.parse(since));
        }
        return vehicleActivityService.getActivitiesByVehicleRef(vehicleRef, direction, null);
    }

    @GetMapping(path = "route/{route}")
    public List<VehicleActivity> getActivitiesByRoute(@PathVariable("route") String route,
                                                      @RequestParam(required = false) String direction,
                                                      @RequestParam(required = false) String since) {
        if (since != null) {
            return vehicleActivityService.getActivitiesByRoute(route, direction, LocalDateTime.parse(since));
        }
        return vehicleActivityService.getActivitiesByRoute(route, direction, null);
    }

}
