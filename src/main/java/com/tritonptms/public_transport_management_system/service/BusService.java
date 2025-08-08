package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Bus;
import java.util.List;
import java.util.Optional;

public interface BusService {
    List<Bus> getAllBuses();
    Optional<Bus> getBusById(Long id);
    Bus saveBus(Bus bus);
    void deleteBus(Long id);
}