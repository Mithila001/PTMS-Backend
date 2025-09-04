package com.tritonptms.public_transport_management_system.controller;

import com.tritonptms.public_transport_management_system.model.ScheduledTrip;
import com.tritonptms.public_transport_management_system.model.enums.route.Direction;
import com.tritonptms.public_transport_management_system.service.ScheduledTripService;
import com.tritonptms.public_transport_management_system.dto.ScheduledTripDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scheduled-trips")
public class ScheduledTripController {

    private final ScheduledTripService scheduledTripService;

    public ScheduledTripController(ScheduledTripService scheduledTripService) {
        this.scheduledTripService = scheduledTripService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduledTripDto>> getAllScheduledTrips() {
        List<ScheduledTripDto> scheduledTrips = scheduledTripService.getAllScheduledTrips();
        return new ResponseEntity<>(scheduledTrips, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledTripDto> getScheduledTripById(@PathVariable Long id) {
        Optional<ScheduledTripDto> scheduledTrip = scheduledTripService.getScheduledTripById(id);
        return scheduledTrip.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ScheduledTripDto> createScheduledTrip(@Valid @RequestBody ScheduledTripDto scheduledTripDto) {
        ScheduledTrip newScheduledTrip = scheduledTripService.saveScheduledTrip(scheduledTripDto);
        ScheduledTripDto newScheduledTripDto = scheduledTripService.convertToDto(newScheduledTrip);
        return new ResponseEntity<>(newScheduledTripDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduledTripDto> updateScheduledTrip(@PathVariable Long id,
            @Valid @RequestBody ScheduledTripDto scheduledTripDetailsDto) {
        scheduledTripDetailsDto.setId(id);
        ScheduledTrip updatedScheduledTrip = scheduledTripService.saveScheduledTrip(scheduledTripDetailsDto);
        ScheduledTripDto updatedScheduledTripDto = scheduledTripService.convertToDto(updatedScheduledTrip);
        return new ResponseEntity<>(updatedScheduledTripDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledTrip(@PathVariable Long id) {
        scheduledTripService.deleteScheduledTrip(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search-trips")
    public ResponseEntity<List<ScheduledTripDto>> searchScheduledTrips(
            @RequestParam(required = false) String scheduledTripId,
            @RequestParam(required = false) Direction direction) {

        List<ScheduledTrip> scheduledTrips = scheduledTripService.searchScheduledTrips(scheduledTripId, direction);
        List<ScheduledTripDto> scheduledTripDtos = scheduledTrips.stream()
                .map(scheduledTripService::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(scheduledTripDtos, HttpStatus.OK);
    }
}