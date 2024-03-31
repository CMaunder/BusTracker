package com.cmaunder.bustracker.vehicleActivity;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VehicleActivityController.class)
public class VehicleActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleActivityService service;

    @Test
    public void getAllActivitiesReturnsAllActivities() throws Exception {
        VehicleActivity vehicleActivity = new VehicleActivity();
        vehicleActivity.setRecordedAtTime(LocalDateTime.now());
        vehicleActivity.setItemIdentifier("1");
        vehicleActivity.setValidUntilTime(LocalDateTime.now());
        when(service.getAllActivities()).thenReturn(Collections.singletonList(vehicleActivity));
        mockMvc.perform(get("/api/v1/activity/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void updateRecordsCallsFetchAndSaveActivityData() throws Exception {
        mockMvc.perform(get("/api/v1/activity/update-records"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully wrote 0 new records to database."));
        verify(service, times(1)).fetchAndSaveActivityData(100000000);
    }
}
