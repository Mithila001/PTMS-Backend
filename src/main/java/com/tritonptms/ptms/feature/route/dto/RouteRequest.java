package com.tritonptms.ptms.feature.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RouteRequest(
        @NotBlank @Size(max = 20) String routeNumber,
        @NotBlank @Size(max = 100) String origin,
        @NotBlank @Size(max = 100) String destination,
        @Size(max = 100) List<@NotBlank @Size(max = 150) String> majorStops,
        String routePath) {
}
