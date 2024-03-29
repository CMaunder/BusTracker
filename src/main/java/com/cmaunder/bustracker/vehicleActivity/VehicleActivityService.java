package com.cmaunder.bustracker.vehicleActivity;

import com.cmaunder.bustracker.utils.RequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.json.XML;

import java.io.IOException;
import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class VehicleActivityService {

    private final RequestService requestService;
    private final VehicleActivityRepository vehicleActivityRepository;

    @Autowired
    public VehicleActivityService(RequestService requestService, VehicleActivityRepository vehicleActivityRepository) {
        this.requestService = requestService;
        this.vehicleActivityRepository = vehicleActivityRepository;
    }

    public int fetchAndSaveActivityData() throws URISyntaxException, IOException, InterruptedException {
        String xmlBody = requestService.get("https://data.bus-data.dft.gov.uk/api/v1/datafeed/7721/");
        JSONObject jsonObject = XML.toJSONObject(xmlBody);
        JSONArray vehicleActivityJson = jsonObject
                .getJSONObject("Siri")
                .getJSONObject("ServiceDelivery")
                .getJSONObject("VehicleMonitoringDelivery")
                .getJSONArray("VehicleActivity");

        // This works because java passes the value of the reference to the vehicleActivityJsonObject
        flattenVehicleActivityJson(vehicleActivityJson);
        ObjectMapper mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build().registerModule(new JavaTimeModule());
        List<VehicleActivity> vehicleActivities = mapper.readValue(vehicleActivityJson.toString(), new TypeReference<>(){});
        long start = System.currentTimeMillis();
        List<VehicleActivity> existingActivities = vehicleActivityRepository.findAll();
        long finish = System.currentTimeMillis();
        long start2 = System.currentTimeMillis();
        List<VehicleActivity> activitiesToSave = new ArrayList<>();
        for (VehicleActivity a: vehicleActivities) {
            boolean dupe = false;
            for (VehicleActivity ea: existingActivities) {
                if (a.getRecordedAtTime().isEqual(ea.getRecordedAtTime()) && Objects.equals(a.getVehicleRef(), ea.getVehicleRef())) {
                    dupe = true;
                }
            }
            if (!dupe) {
                activitiesToSave.add(a);
            }
        }
        long finish2 = System.currentTimeMillis();

        long timeElapsed = finish - start;
        long timeElapsed2 = finish2 - start2;
        System.out.println("It took " + timeElapsed + " milliseconds to fetch and " + timeElapsed2 + " milliseconds" +
                " to process dupe checking in code.");
        return vehicleActivityRepository.saveAll(activitiesToSave).size();
    }

    private static void flattenVehicleActivityJson(JSONArray vehicleActivityJson) {
        JSONArray myNewJsonArray = new JSONArray();
        for (int i = 0; i < vehicleActivityJson.length(); i++) {
            JSONObject vehicleActivityObject = vehicleActivityJson.getJSONObject(i);
            JSONObject monitoredVehicleJourney = vehicleActivityObject.getJSONObject("MonitoredVehicleJourney");

            JSONObject vehicleLocation = monitoredVehicleJourney.getJSONObject("VehicleLocation");
            Iterator<String> vlKeys = vehicleLocation.keys();
            while(vlKeys.hasNext()) {
                String key = vlKeys.next();
                monitoredVehicleJourney.put(key, vehicleLocation.get(key));
            }
            monitoredVehicleJourney.remove("VehicleLocation");
            myNewJsonArray.put(monitoredVehicleJourney);

            Iterator<String> mvjKeys = monitoredVehicleJourney.keys();
            while(mvjKeys.hasNext()) {
                String key = mvjKeys.next();
                vehicleActivityObject.put(key, monitoredVehicleJourney.get(key));
            }
            vehicleActivityObject.remove("MonitoredVehicleJourney");
            myNewJsonArray.put(vehicleActivityObject);
        }
    }


    public List<VehicleActivity> getAllActivities() {
        return vehicleActivityRepository.findAll();
    }

    public List<VehicleActivity> getActivitiesByVehicleRef(String vehicleRef, String direction, LocalDateTime since) {
        List<VehicleActivity> vehicleActivityData;
        if (since != null) {
            vehicleActivityData = vehicleActivityRepository.findAllActivitiesByVehicleRefSince(vehicleRef, since);
        } else {
            vehicleActivityData = vehicleActivityRepository
                    .findAllByVehicleRefOrderByRecordedAtTimeDesc(vehicleRef);
        }
        if (direction == null) {
            return vehicleActivityData;
        }
        return filterByDirection(vehicleActivityData, Direction.valueOf(direction));
    }

    public List<VehicleActivity> getActivitiesByRoute(String route, String direction, LocalDateTime since){
        List<VehicleActivity> vehicleActivityData;
        if (since != null) {
            vehicleActivityData = vehicleActivityRepository.findAllActivitiesByRouteSince(route, since);
        } else {
            vehicleActivityData = vehicleActivityRepository
                    .findAllByPublishedLineNameOrderByRecordedAtTimeDesc(route);
        }
        if (direction == null) {
            return vehicleActivityData;
        }
        return filterByDirection(vehicleActivityData, Direction.valueOf(direction));
    }

    private List<VehicleActivity> filterByDirection(List<VehicleActivity> vehicleActivityData, Direction direction) {
        return vehicleActivityData.stream().filter(a -> a.getDirectionRef() == direction).collect(Collectors.toList());
    }

}
