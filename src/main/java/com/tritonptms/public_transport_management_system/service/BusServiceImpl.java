package com.tritonptms.public_transport_management_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.public_transport_management_system.dto.LogDetail;
import com.tritonptms.public_transport_management_system.exception.ResourceNotFoundException;
import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import com.tritonptms.public_transport_management_system.service.specification.BusSpecification;
import com.tritonptms.public_transport_management_system.utils.ObjectComparisonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private final ActionLogService actionLogService;
    private final ObjectMapper objectMapper;

    private final BusRepository busRepository;

    @Autowired
    public BusServiceImpl(BusRepository busRepository, ActionLogService actionLogService, ObjectMapper objectMapper) {
        this.busRepository = busRepository;
        this.actionLogService = actionLogService;
        this.objectMapper = objectMapper;
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
        // This method will now only handle the creation logic
        Bus savedBus = busRepository.save(bus);

        return savedBus;
    }

    @Override
    public Bus updateBus(Long id, Bus busDetails) {
        // Find the existing bus or throw an exception
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));

        // Create a deep copy of the original entity for comparison
        Bus oldBus = new Bus();
        oldBus.setRegistrationNumber(existingBus.getRegistrationNumber());
        oldBus.setMake(existingBus.getMake());
        oldBus.setModel(existingBus.getModel());
        oldBus.setYearOfManufacture(existingBus.getYearOfManufacture());
        oldBus.setFuelType(existingBus.getFuelType());
        oldBus.setActive(existingBus.isActive());
        oldBus.setSeatingCapacity(existingBus.getSeatingCapacity());
        oldBus.setStandingCapacity(existingBus.getStandingCapacity());
        oldBus.setNtcPermitNumber(existingBus.getNtcPermitNumber());
        oldBus.setComfortType(existingBus.getComfortType());
        oldBus.setIsA_C(existingBus.getIsA_C());
        oldBus.setServiceType(existingBus.getServiceType());

        // Update the existing entity with new data
        existingBus.setRegistrationNumber(busDetails.getRegistrationNumber());
        existingBus.setMake(busDetails.getMake());
        existingBus.setModel(busDetails.getModel());
        existingBus.setYearOfManufacture(busDetails.getYearOfManufacture());
        existingBus.setFuelType(busDetails.getFuelType());
        existingBus.setActive(busDetails.isActive());
        existingBus.setSeatingCapacity(busDetails.getSeatingCapacity());
        existingBus.setStandingCapacity(busDetails.getStandingCapacity());
        existingBus.setNtcPermitNumber(busDetails.getNtcPermitNumber());
        existingBus.setComfortType(busDetails.getComfortType());
        existingBus.setIsA_C(busDetails.getIsA_C());
        existingBus.setServiceType(busDetails.getServiceType());

        Bus updatedBus = busRepository.save(existingBus);

        return updatedBus;
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