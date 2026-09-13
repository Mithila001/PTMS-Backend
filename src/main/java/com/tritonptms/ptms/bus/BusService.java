package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.Bus.ServiceType;
import com.tritonptms.ptms.bus.dto.BusRequest;
import com.tritonptms.ptms.bus.dto.BusResponse;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    public BusService(BusRepository busRepository, BusMapper busMapper) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
    }

    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream().map(busMapper::toResponse).toList();
    }

    public BusResponse getBusById(Long id) {
        return busMapper.toResponse(findBus(id));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public BusResponse createBus(BusRequest request) {
        return busMapper.toResponse(busRepository.save(busMapper.fromRequest(request)));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public BusResponse updateBus(Long id, BusRequest request) {
        Bus bus = findBus(id);
        busMapper.apply(bus, request);
        return busMapper.toResponse(busRepository.save(bus));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public void deleteBus(Long id) {
        busRepository.delete(findBus(id));
    }

    public Page<BusResponse> searchBuses(String registrationNumber, ServiceType serviceType, Pageable pageable) {
        Specification<Bus> specification = Specification.allOf(
                BusSpecification.hasRegistrationNumber(registrationNumber),
                BusSpecification.hasServiceType(serviceType));
        return busRepository.findAll(specification, pageable).map(busMapper::toResponse);
    }

    private Bus findBus(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
    }
}
