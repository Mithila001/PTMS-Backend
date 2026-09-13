package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.dto.BusRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusMapperTest {

    private final BusMapper mapper = new BusMapper();

    @Test
    void mapsAndNormalizesBusRequest() {
        BusRequest request = new BusRequest(
                " ND-2001 ", " Ashok Leyland ", " Viking ", 2024,
                Bus.FuelType.DIESEL, true, 45, 15, 2000000001L,
                Bus.ComfortType.NORMAL, false, Bus.ServiceType.PRIVATE_BUS);

        Bus bus = mapper.fromRequest(request);

        assertThat(bus.getRegistrationNumber()).isEqualTo("ND-2001");
        assertThat(bus.getMake()).isEqualTo("Ashok Leyland");
        assertThat(bus.getModel()).isEqualTo("Viking");
        assertThat(bus.getSeatingCapacity()).isEqualTo(45);
        assertThat(bus.isActive()).isTrue();
    }
}
