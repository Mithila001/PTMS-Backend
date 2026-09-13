package com.tritonptms.ptms.feature.route.dto;

import java.util.List;

public record RouteResponse(
        Long id,
        String routeNumber,
        String origin,
        String destination,
        List<String> majorStops,
        String routePath) {
}
