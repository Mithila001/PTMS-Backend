package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.dto.BusDto;
import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BusService {
    List<Bus> getAllBuses();

    Optional<Bus> getBusById(Long id);

    Bus saveBus(Bus bus);

    Bus updateBus(Long id, Bus busDetails); // <-- Added this method

    void deleteBus(Long id);

    Page<BusDto> searchBuses(String registrationNumber, ServiceType serviceType, Pageable pageable);
}