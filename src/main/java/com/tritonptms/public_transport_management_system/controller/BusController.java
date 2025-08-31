package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.Bus;
import com.tritonptms.public_transport_management_system.model.Bus.ServiceType;
import com.tritonptms.public_transport_management_system.service.BusService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    // Use constructor injection to get an instance of the BusService
    public BusController(BusService busService) {
        this.busService = busService;
    }

    // GET /api/buses: Retrieves a list of all buses
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {
        List<Bus> buses = busService.getAllBuses();
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    // GET /api/buses/{id}: Retrieves a single bus by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        Optional<Bus> bus = busService.getBusById(id);
        return bus.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /api/buses: Creates a new bus
    @PostMapping
    public ResponseEntity<Bus> createBus(@Valid @RequestBody Bus bus) {
        Bus newBus = busService.saveBus(bus);
        return new ResponseEntity<>(newBus, HttpStatus.CREATED);
    }

    // PUT /api/buses/{id}: Updates an existing bus
    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @Valid @RequestBody Bus busDetails) {
        // Here you would add logic to check if the bus with the ID exists
        // and then update it with the new details.
        busDetails.setId(id);
        Bus updatedBus = busService.saveBus(busDetails);
        return new ResponseEntity<>(updatedBus, HttpStatus.OK);
    }

    // DELETE /api/buses/{id}: Deletes a bus
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Bus>> searchBuses(
            @RequestParam(required = false) String registrationNumber,
            @RequestParam(required = false) ServiceType serviceType) {

        List<Bus> buses = busService.searchBuses(registrationNumber, serviceType);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

}
