package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.dto.BusRequest;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    private final BusMapper busMapper = new BusMapper();

    @Test
    void missingBusProducesDomainNotFoundError() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());
        BusService service = new BusService(busRepository, busMapper);

        assertThatThrownBy(() -> service.getBusById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateAppliesNormalizedRequestToExistingEntity() {
        Bus existing = new Bus();
        existing.setId(7L);
        existing.setRegistrationNumber("OLD-1");
        when(busRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(busRepository.save(any(Bus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BusService service = new BusService(busRepository, busMapper);

        BusRequest request = new BusRequest(
                " ND-3001 ", " Tata ", " LP 909 ", 2023,
                Bus.FuelType.DIESEL, true, 40, 10, 3000000001L,
                Bus.ComfortType.NORMAL, false, Bus.ServiceType.PRIVATE_BUS);

        var response = service.updateBus(7L, request);

        assertThat(response.registrationNumber()).isEqualTo("ND-3001");
        assertThat(response.make()).isEqualTo("Tata");
        assertThat(response.id()).isEqualTo(7L);
    }
}
