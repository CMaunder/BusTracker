package com.cmaunder.bustracker.vehicleActivity;

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
    private ArgumentCaptor<List<VehicleActivity>> vehicleActivityCaptor;

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
        verify(repository, times(1)).saveAll(vehicleActivityCaptor.capture());
        assertEquals(Arrays.asList(getVehicleActivity("109c5cae-df96-4c1e-88aa-9f81d1198142",
                                Direction.OUTBOUND,
                                "1995-03-25T00:00:00"),
                        getVehicleActivity("109c5cae-df96-4c1e-88aa-9f81d1198143")).toString(),
                vehicleActivityCaptor.getAllValues().getFirst().toString());
    }

    @Test
    public void testFetchAndSaveActivityDataCallsSaveAllWithUniqueConstraintActivitiesFromGivenXml() throws URISyntaxException, IOException, InterruptedException {
        String myXml = IOUtils.toString(
                Objects.requireNonNull(this.getClass().getResourceAsStream("/stubbedXmlExample.txt")),
                StandardCharsets.UTF_8
        );
        when(requestService.get(anyString())).thenReturn(myXml);
        when(repository.findAll()).thenReturn(List.of(getVehicleActivity("blahblah")));
        vehicleActivityService.fetchAndSaveActivityData();
        verify(repository, times(1)).saveAll(vehicleActivityCaptor.capture());
        assertEquals(List.of(getVehicleActivity("109c5cae-df96-4c1e-88aa-9f81d1198142",
                        Direction.OUTBOUND,
                        "1995-03-25T00:00:00")).toString(),
                vehicleActivityCaptor.getAllValues().getFirst().toString());
    }

    @Test
    public void testGetActivitiesByVehicleRefReturnsAllActivitiesWithVehicleRefWhenDirectionAndSinceAreNull() {
        VehicleActivity vehicleActivity1 = getVehicleActivity("1", Direction.INBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity2 = getVehicleActivity("2", Direction.OUTBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        when(repository.findAllByVehicleRefOrderByRecordedAtTimeDesc("1557")).thenReturn(Arrays.asList(vehicleActivity1,
                vehicleActivity2, vehicleActivity3, vehicleActivity4));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByVehicleRef("1557", null, null);

        assertEquals(4, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByVehicleRefReturnsFiltersByDirection() {
        VehicleActivity vehicleActivity1 = getVehicleActivity("1", Direction.INBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity2 = getVehicleActivity("2", Direction.OUTBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        when(repository.findAllByVehicleRefOrderByRecordedAtTimeDesc("1557"))
                .thenReturn(Arrays.asList(vehicleActivity1, vehicleActivity2, vehicleActivity3, vehicleActivity4));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByVehicleRef("1557", "INBOUND", null);

        assertEquals(2, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByVehicleRefReturnsFiltersBySince() {
        LocalDateTime ldt = LocalDateTime.parse("2024-01-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity5 = getVehicleActivity("5", Direction.INBOUND, "2020-03-29T10:46:11");
        when(repository.findAllActivitiesByVehicleRefSince("1557", ldt))
                .thenReturn(Arrays.asList(vehicleActivity3, vehicleActivity4, vehicleActivity5));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByVehicleRef("1557", null, ldt);

        assertEquals(3, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByVehicleRefReturnsFiltersBySinceAndDirection() {
        LocalDateTime ldt = LocalDateTime.parse("2024-01-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity5 = getVehicleActivity("5", Direction.INBOUND, "2020-03-29T10:46:11");
        when(repository.findAllActivitiesByVehicleRefSince("1557", ldt))
                .thenReturn(Arrays.asList(vehicleActivity3, vehicleActivity4, vehicleActivity5));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByVehicleRef("1557", "INBOUND", ldt);

        assertEquals(2, vehicleActivityList.size());
    }


    @Test
    public void testGetActivitiesByRouteReturnsAllActivitiesWithVehicleRefWhenDirectionAndSinceAreNull() {
        VehicleActivity vehicleActivity1 = getVehicleActivity("1", Direction.INBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity2 = getVehicleActivity("2", Direction.OUTBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        when(repository.findAllByPublishedLineNameOrderByRecordedAtTimeDesc("X3")).thenReturn(Arrays.asList(vehicleActivity1,
                vehicleActivity2, vehicleActivity3, vehicleActivity4));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByRoute("X3", null, null);

        assertEquals(4, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByRouteReturnsFiltersByDirection() {
        VehicleActivity vehicleActivity1 = getVehicleActivity("1", Direction.INBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity2 = getVehicleActivity("2", Direction.OUTBOUND, "2024-03-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        when(repository.findAllByPublishedLineNameOrderByRecordedAtTimeDesc("X3"))
                .thenReturn(Arrays.asList(vehicleActivity1, vehicleActivity2, vehicleActivity3, vehicleActivity4));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByRoute("X3", "INBOUND", null);

        assertEquals(2, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByRouteReturnsFiltersBySince() {
        LocalDateTime ldt = LocalDateTime.parse("2024-01-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity5 = getVehicleActivity("5", Direction.INBOUND, "2020-03-29T10:46:11");
        when(repository.findAllActivitiesByRouteSince("X3", ldt))
                .thenReturn(Arrays.asList(vehicleActivity3, vehicleActivity4, vehicleActivity5));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByRoute("X3", null, ldt);

        assertEquals(3, vehicleActivityList.size());
    }

    @Test
    public void testGetActivitiesByRouteReturnsFiltersBySinceAndDirection() {
        LocalDateTime ldt = LocalDateTime.parse("2024-01-29T10:46:11");
        VehicleActivity vehicleActivity3 = getVehicleActivity("3", Direction.OUTBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity4 = getVehicleActivity("4", Direction.INBOUND, "2023-03-29T10:46:11");
        VehicleActivity vehicleActivity5 = getVehicleActivity("5", Direction.INBOUND, "2020-03-29T10:46:11");
        when(repository.findAllActivitiesByRouteSince("X3", ldt))
                .thenReturn(Arrays.asList(vehicleActivity3, vehicleActivity4, vehicleActivity5));

        List<VehicleActivity> vehicleActivityList = vehicleActivityService
                .getActivitiesByRoute("X3", "INBOUND", ldt);

        assertEquals(2, vehicleActivityList.size());
    }

    @NotNull
    private static VehicleActivity getVehicleActivity(String id, Direction direction, String recordedAtTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(recordedAtTime);
        return new VehicleActivity(
                localDateTime,
                id,
                LocalDateTime.of(1995, Month.MARCH, 24, 1, 0),
                direction,
                "17",
                "BLUS",
                "Barnfield_Road",
                "Adanac_Park",
                "50.93528",
                "-1.467218",
                "74022",
                "1557",
                0.0);
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
                "Barnfield_Road",
                "Adanac_Park",
                "50.93528",
                "-1.467218",
                "74022",
                "1557",
                0.0);
    }


}
