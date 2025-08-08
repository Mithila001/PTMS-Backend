package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {
    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusServiceImpl busService;

    private Bus bus;

    @BeforeEach
    void setup() {
        bus = new Bus();
        bus.setId(1L);
        bus.setMake("Ashok Leyland");
    }

    @Test
    void whenSaveBus_thenReturnBus() {
        // Given
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // When
        Bus savedBus = busService.saveBus(bus);

        // Then
        assertEquals(bus.getMake(), savedBus.getMake());
        verify(busRepository, times(1)).save(bus);
    }

    @Test
    void whenGetBusById_thenReturnBus() {
        // Given
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        // When
        Optional<Bus> foundBus = busService.getBusById(1L);

        // Then
        assertEquals(bus.getMake(), foundBus.get().getMake());
        verify(busRepository, times(1)).findById(1L);
    }
    
}
