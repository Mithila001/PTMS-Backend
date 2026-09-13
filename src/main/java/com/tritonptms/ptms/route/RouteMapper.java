package com.tritonptms.ptms.route;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.route.dto.RouteRequest;
import com.tritonptms.ptms.route.dto.RouteResponse;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteMapper {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Route fromRequest(RouteRequest request) {
        Route route = new Route();
        apply(route, request);
        return route;
    }

    public void apply(Route route, RouteRequest request) {
        route.setRouteNumber(request.routeNumber().trim());
        route.setOrigin(request.origin().trim());
        route.setDestination(request.destination().trim());
        List<String> requestedStops = request.majorStops() == null
                ? new ArrayList<>()
                : request.majorStops().stream().map(String::trim).toList();
        if (route.getMajorStops() == null) {
            route.setMajorStops(new ArrayList<>(requestedStops));
        } else {
            route.getMajorStops().clear();
            route.getMajorStops().addAll(requestedStops);
        }
        route.setRoutePath(parseRoutePath(request.routePath()));
    }

    public RouteResponse toResponse(Route route) {
        String routePath = route.getRoutePath() == null ? null : new WKTWriter().write(route.getRoutePath());
        List<String> stops = route.getMajorStops() == null ? List.of() : List.copyOf(route.getMajorStops());
        return new RouteResponse(
                route.getId(),
                route.getRouteNumber(),
                route.getOrigin(),
                route.getDestination(),
                stops,
                routePath);
    }

    private LineString parseRoutePath(String wkt) {
        if (wkt == null || wkt.isBlank()) {
            return null;
        }
        try {
            Geometry geometry = new WKTReader(geometryFactory).read(wkt.trim());
            if (!(geometry instanceof LineString lineString)) {
                throw new BadRequestException("routePath must be a WKT LINESTRING.");
            }
            lineString.setSRID(4326);
            return lineString;
        } catch (ParseException ex) {
            throw new BadRequestException("routePath must contain valid WKT LINESTRING data.");
        }
    }
}
