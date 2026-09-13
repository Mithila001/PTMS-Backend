package com.tritonptms.ptms.schedule;

import com.tritonptms.ptms.schedule.dto.ScheduledTripRequest;
import com.tritonptms.ptms.schedule.dto.ScheduledTripResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/scheduled-trips")
public class ScheduledTripController {

    private final ScheduledTripService scheduledTripService;

    public ScheduledTripController(ScheduledTripService scheduledTripService) {
        this.scheduledTripService = scheduledTripService;
    }

    @GetMapping
    public List<ScheduledTripResponse> getAllScheduledTrips() {
        return scheduledTripService.getAllScheduledTrips();
    }

    @GetMapping("/{id}")
    public ScheduledTripResponse getScheduledTripById(@PathVariable Long id) {
        return scheduledTripService.getScheduledTripById(id);
    }

    @PostMapping
    public ResponseEntity<ScheduledTripResponse> createScheduledTrip(
            @Valid @RequestBody ScheduledTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduledTripService.createScheduledTrip(request));
    }

    @PutMapping("/{id}")
    public ScheduledTripResponse updateScheduledTrip(@PathVariable Long id,
            @Valid @RequestBody ScheduledTripRequest request) {
        return scheduledTripService.updateScheduledTrip(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledTrip(@PathVariable Long id) {
        scheduledTripService.deleteScheduledTrip(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ScheduledTripResponse> searchScheduledTrips(
            @RequestParam(required = false) String routeNumber,
            @RequestParam(required = false) Direction direction) {
        return scheduledTripService.searchScheduledTrips(routeNumber, direction);
    }
}
