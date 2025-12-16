package com.revtickets.service;

import com.revtickets.model.Travel;
import com.revtickets.repository.TravelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

    @Mock
    private TravelRepository travelRepository;

    @InjectMocks
    private TravelService travelService;

    private Travel travel1;
    private Travel travel2;
    private List<Travel> travelList;

    @BeforeEach
    void setUp() {
        travel1 = new Travel();
        travel1.setId(1L);
        travel1.setType("flight");
        travel1.setOperator("IndiGo");
        travel1.setVehicleNumber("6E-123");
        travel1.setDeparture("Delhi");
        travel1.setArrival("Mumbai");
        travel1.setPrice(4500.0);
        travel1.setAvailableSeats(45);

        travel2 = new Travel();
        travel2.setId(2L);
        travel2.setType("bus");
        travel2.setOperator("Volvo");
        travel2.setVehicleNumber("KA-01-AB-1234");
        travel2.setDeparture("Delhi");
        travel2.setArrival("Mumbai");
        travel2.setPrice(1200.0);
        travel2.setAvailableSeats(25);

        travelList = Arrays.asList(travel1, travel2);
    }

    @Test
    void testSearchTravel_WithTypeAndFromAndTo() {
        when(travelRepository.findByTypeAndDepartureAndArrival("flight", "Delhi", "Mumbai"))
            .thenReturn(Arrays.asList(travel1));

        List<Travel> result = travelService.searchTravel("flight", "Delhi", "Mumbai");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("flight", result.get(0).getType());
        assertEquals("Delhi", result.get(0).getDeparture());
        assertEquals("Mumbai", result.get(0).getArrival());

        verify(travelRepository, times(1))
            .findByTypeAndDepartureAndArrival("flight", "Delhi", "Mumbai");
        verify(travelRepository, never()).findByDepartureAndArrival(anyString(), anyString());
        verify(travelRepository, never()).findByType(anyString());
        verify(travelRepository, never()).findAll();
    }

    @Test
    void testSearchTravel_WithFromAndToOnly() {
        when(travelRepository.findByDepartureAndArrival("Delhi", "Mumbai"))
            .thenReturn(travelList);

        List<Travel> result = travelService.searchTravel(null, "Delhi", "Mumbai");

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(travelRepository, times(1)).findByDepartureAndArrival("Delhi", "Mumbai");
        verify(travelRepository, never()).findByTypeAndDepartureAndArrival(anyString(), anyString(), anyString());
    }

    @Test
    void testSearchTravel_WithTypeOnly() {
        when(travelRepository.findByType("flight")).thenReturn(Arrays.asList(travel1));

        List<Travel> result = travelService.searchTravel("flight", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("flight", result.get(0).getType());

        verify(travelRepository, times(1)).findByType("flight");
        verify(travelRepository, never()).findByDepartureAndArrival(anyString(), anyString());
    }

    @Test
    void testSearchTravel_NoParameters() {
        when(travelRepository.findAll()).thenReturn(travelList);

        List<Travel> result = travelService.searchTravel(null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(travelRepository, times(1)).findAll();
        verify(travelRepository, never()).findByType(anyString());
        verify(travelRepository, never()).findByDepartureAndArrival(anyString(), anyString());
    }

    @Test
    void testCreateTravel_Success() {
        Travel newTravel = new Travel();
        newTravel.setType("train");
        newTravel.setOperator("Rajdhani Express");
        newTravel.setDeparture("Delhi");
        newTravel.setArrival("Mumbai");
        newTravel.setPrice(2500.0);

        when(travelRepository.save(any(Travel.class))).thenAnswer(invocation -> {
            Travel saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        Travel result = travelService.createTravel(newTravel);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("train", result.getType());
        assertEquals("Rajdhani Express", result.getOperator());

        verify(travelRepository, times(1)).save(newTravel);
    }

    @Test
    void testUpdateTravel_Success() {
        travel1.setPrice(5000.0);

        when(travelRepository.existsById(1L)).thenReturn(true);
        when(travelRepository.save(any(Travel.class))).thenReturn(travel1);

        Travel result = travelService.updateTravel(travel1);

        assertNotNull(result);
        assertEquals(5000.0, result.getPrice());

        verify(travelRepository, times(1)).existsById(1L);
        verify(travelRepository, times(1)).save(travel1);
    }

    @Test
    void testUpdateTravel_TravelNotFound() {
        when(travelRepository.existsById(999L)).thenReturn(false);

        Travel nonExistentTravel = new Travel();
        nonExistentTravel.setId(999L);

        assertThrows(RuntimeException.class, () -> {
            travelService.updateTravel(nonExistentTravel);
        });

        verify(travelRepository, times(1)).existsById(999L);
        verify(travelRepository, never()).save(any(Travel.class));
    }

    @Test
    void testDeleteTravel_Success() {
        when(travelRepository.existsById(1L)).thenReturn(true);
        doNothing().when(travelRepository).deleteById(1L);

        travelService.deleteTravel(1L);

        verify(travelRepository, times(1)).existsById(1L);
        verify(travelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTravel_TravelNotFound() {
        when(travelRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            travelService.deleteTravel(999L);
        });

        verify(travelRepository, times(1)).existsById(999L);
        verify(travelRepository, never()).deleteById(anyLong());
    }
}

