package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.dto.BusDto;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.bus.Bus.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BusService {

    private final BusRepository busRepository;
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;

    }
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }
    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }
    @Transactional
    public Bus saveBus(Bus bus) {
        // This method will now only handle the creation logic
        Bus savedBus = busRepository.save(bus);

        return savedBus;
    }
    @Transactional
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
    @Transactional
    public void deleteBus(Long id) {

        busRepository.deleteById(id);

    }

    private BusDto convertToDto(Bus bus) {
        BusDto busDto = new BusDto();
        busDto.setId(bus.getId());
        busDto.setRegistrationNumber(bus.getRegistrationNumber());
        busDto.setMake(bus.getMake());
        busDto.setModel(bus.getModel());
        busDto.setYearOfManufacture(bus.getYearOfManufacture());
        busDto.setFuelType(bus.getFuelType());
        busDto.setActive(bus.isActive());
        busDto.setSeatingCapacity(bus.getSeatingCapacity());
        busDto.setStandingCapacity(bus.getStandingCapacity());
        busDto.setNtcPermitNumber(bus.getNtcPermitNumber());
        busDto.setComfortType(bus.getComfortType());
        busDto.setIsA_C(bus.getIsA_C());
        busDto.setServiceType(bus.getServiceType());
        return busDto;
    }

    // Search method
    public Page<BusDto> searchBuses(String registrationNumber, ServiceType serviceType, Pageable pageable) {
        Specification<Bus> combinedSpec = Specification.allOf(
                BusSpecification.hasRegistrationNumber(registrationNumber),
                BusSpecification.hasServiceType(serviceType));

        Page<Bus> busPage = busRepository.findAll(combinedSpec, pageable);
        return busPage.map(this::convertToDto);
    }
}
