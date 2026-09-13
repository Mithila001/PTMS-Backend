package com.tritonptms.ptms.route;

import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.route.dto.RouteRequest;
import com.tritonptms.ptms.route.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    public RouteService(RouteRepository routeRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream().map(routeMapper::toResponse).toList();
    }

    public RouteResponse getRouteById(Long id) {
        return routeMapper.toResponse(findRoute(id));
    }

    public RouteResponse getRouteByNumber(String routeNumber) {
        Route route = routeRepository.findByRouteNumber(routeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with number: " + routeNumber));
        return routeMapper.toResponse(route);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public RouteResponse createRoute(RouteRequest request) {
        return routeMapper.toResponse(routeRepository.save(routeMapper.fromRequest(request)));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = findRoute(id);
        routeMapper.apply(route, request);
        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public void deleteRoute(Long id) {
        routeRepository.delete(findRoute(id));
    }

    public Page<RouteResponse> searchRoutes(String routeNumber, String origin, String destination, Pageable pageable) {
        Specification<Route> specification = Specification.allOf(
                RouteSpecification.hasRouteNumber(routeNumber),
                RouteSpecification.hasOrigin(origin),
                RouteSpecification.hasDestination(destination));
        return routeRepository.findAll(specification, pageable).map(routeMapper::toResponse);
    }

    private Route findRoute(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }
}
