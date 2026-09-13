package com.tritonptms.ptms.bus;

import com.tritonptms.ptms.bus.Bus.ServiceType;
import com.tritonptms.ptms.bus.dto.BusRequest;
import com.tritonptms.ptms.bus.dto.BusResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public List<BusResponse> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{id}")
    public BusResponse getBusById(@PathVariable Long id) {
        return busService.getBusById(id);
    }

    @PostMapping
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody BusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.createBus(request));
    }

    @PutMapping("/{id}")
    public BusResponse updateBus(@PathVariable Long id, @Valid @RequestBody BusRequest request) {
        return busService.updateBus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<BusResponse> searchBuses(
            @RequestParam(required = false) String registrationNumber,
            @RequestParam(required = false) ServiceType serviceType,
            Pageable pageable) {
        return busService.searchBuses(registrationNumber, serviceType, pageable);
    }
}
