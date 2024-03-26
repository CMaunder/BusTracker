package com.cmaunder.bustracker.vehicleActivity;

import com.cmaunder.bustracker.utils.Direction;
import com.cmaunder.bustracker.utils.RequestService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class VehicleActivityServiceTest {

    @Mock
    VehicleActivityRepository repository;

    @Captor
    private ArgumentCaptor<VehicleActivity> vehicleActivityCaptor;

    @Mock
    RequestService requestService;

    @InjectMocks
    VehicleActivityService vehicleActivityService;

    @Test
    public void testGetAllActivities() {
        VehicleActivity vehicleActivity1 = getVehicleActivity("1");
        VehicleActivity vehicleActivity2 = getVehicleActivity("2");

        when(repository.findAll()).thenReturn(Arrays.asList(vehicleActivity1, vehicleActivity2));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService.getAllActivities();
        assertEquals(vehicleActivityList.size(), 2);
        assertEquals(vehicleActivityList.getFirst().getItemIdentifier(), "1");
        assertEquals(vehicleActivityList.get(1).getItemIdentifier(), "2");
    }

    @Test
    public void testFetchAndSaveActivityDataCallsSaveAllWithActivitiesFromGivenXml() throws URISyntaxException, IOException, InterruptedException {
        String myXml = IOUtils.toString(
                Objects.requireNonNull(this.getClass().getResourceAsStream("/stubbedXmlExample.txt")),
                StandardCharsets.UTF_8
        );
        when(requestService.get(anyString())).thenReturn(myXml);
        vehicleActivityService.fetchAndSaveActivityData();
        verify(repository, times(2)).save(vehicleActivityCaptor.capture());
        assertEquals(getVehicleActivity("109c5cae-df96-4c1e-88aa-9f81d1198142").toString(), vehicleActivityCaptor.getAllValues().get(0).toString());
        assertEquals(getVehicleActivity("109c5cae-df96-4c1e-88aa-9f81d1198143").toString(), vehicleActivityCaptor.getAllValues().get(1).toString());
    }

    @NotNull
    private static VehicleActivity getVehicleActivity(String id) {

        return new VehicleActivity(
                LocalDateTime.of(1995, Month.MARCH, 24, 0, 0),
                id,
                LocalDateTime.of(1995, Month.MARCH, 24, 1, 0),
                Direction.OUTBOUND,
                "17",
                "BLUS",
                "1980SN120747",
                "Barnfield_Road",
                "1900HAA91092",
                "Adanac_Park",
                "50.93528",
                "-1.467218",
                "74022",
                "1557",
                0.0,
                "17");
    }


}
