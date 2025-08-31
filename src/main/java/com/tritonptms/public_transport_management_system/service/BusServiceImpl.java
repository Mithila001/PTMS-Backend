package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import com.tritonptms.public_transport_management_system.service.specification.BusSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;

    @Autowired
    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @Override
    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }

    @Override
    public Bus saveBus(Bus bus) {
        // Here you would add your business logic, e.g., validation.
        // For now, we'll just save it directly.
        return busRepository.save(bus);
    }

    @Override
    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }

    // Search method
    @Override
    public List<Bus> searchBuses(String registrationNumber, ServiceType serviceType) {
        return busRepository.findAll(Specification.allOf(
                BusSpecification.hasRegistrationNumber(registrationNumber),
                BusSpecification.hasServiceType(serviceType)));
    }
}