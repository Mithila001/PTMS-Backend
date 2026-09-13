package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.dto.BusRequest;
import com.tritonptms.ptms.bus.dto.BusResponse;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {

    public Bus fromRequest(BusRequest request) {
        Bus bus = new Bus();
        apply(bus, request);
        return bus;
    }

    public void apply(Bus bus, BusRequest request) {
        bus.setRegistrationNumber(request.registrationNumber().trim());
        bus.setMake(request.make().trim());
        bus.setModel(request.model().trim());
        bus.setYearOfManufacture(request.yearOfManufacture());
        bus.setFuelType(request.fuelType());
        bus.setActive(request.active());
        bus.setSeatingCapacity(request.seatingCapacity());
        bus.setStandingCapacity(request.standingCapacity());
        bus.setNtcPermitNumber(request.ntcPermitNumber());
        bus.setComfortType(request.comfortType());
        bus.setIsA_C(request.airConditioned());
        bus.setServiceType(request.serviceType());
    }

    public BusResponse toResponse(Bus bus) {
        return new BusResponse(
                bus.getId(),
                bus.getRegistrationNumber(),
                bus.getMake(),
                bus.getModel(),
                bus.getYearOfManufacture(),
                bus.getFuelType(),
                bus.isActive(),
                bus.getSeatingCapacity(),
                bus.getStandingCapacity(),
                bus.getNtcPermitNumber(),
                bus.getComfortType(),
                bus.getIsA_C(),
                bus.getServiceType());
    }
}
