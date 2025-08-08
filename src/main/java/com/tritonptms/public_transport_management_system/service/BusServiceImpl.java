package com.tritonptms.public_transport_management_system.service;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}