package com.tritonptms.ptms.infrastructure.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @NotNull @Valid Cors cors,
        @NotNull @Valid Seed seed) {

    public record Cors(@NotEmpty List<@NotBlank String> allowedOrigins) {
    }

    public record Seed(boolean enabled) {
    }
}
