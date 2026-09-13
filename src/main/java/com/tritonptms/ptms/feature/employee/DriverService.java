package com.tritonptms.ptms.feature.employee;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.feature.employee.dto.DriverRequest;
import com.tritonptms.ptms.feature.employee.dto.DriverResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public DriverService(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream().map(driverMapper::toResponse).toList();
    }

    public DriverResponse getDriverById(Long id) {
        return driverMapper.toResponse(findDriver(id));
    }

    public DriverResponse getDriverByNic(String nicNumber) {
        Driver driver = driverRepository.findByNicNumber(nicNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with NIC: " + nicNumber));
        return driverMapper.toResponse(driver);
    }

    @Transactional
    public DriverResponse createDriver(DriverRequest request) {
        return driverMapper.toResponse(driverRepository.save(driverMapper.fromRequest(request)));
    }

    @Transactional
    public DriverResponse updateDriver(Long id, DriverRequest request) {
        Driver driver = findDriver(id);
        driverMapper.apply(driver, request);
        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Transactional
    public void deleteDriver(Long id) {
        driverRepository.delete(findDriver(id));
    }

    private Driver findDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }
}
