package com.tritonptms.ptms.route;

import com.tritonptms.ptms.common.audit.AuditableEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.LineString;

import java.util.List;

@Entity
@Table(name = "routes", indexes = {
        @Index(name = "idx_routes_origin", columnList = "origin"),
        @Index(name = "idx_routes_destination", columnList = "destination")
})
public class Route extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Route number is mandatory")
    @Size(max = 20, message = "Route number cannot exceed 20 characters")
    @Column(name = "route_number", nullable = false, unique = true, length = 20)
    private String routeNumber;

    @NotBlank(message = "Origin is mandatory")
    @Size(max = 100, message = "Origin cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String origin;

    @NotBlank(message = "Destination is mandatory")
    @Size(max = 100, message = "Destination cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String destination;

    @ElementCollection
    @CollectionTable(name = "route_stops", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "stop_order")
    @Column(name = "stop_name", nullable = false, length = 150)
    private List<String> majorStops;

    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    @Column(name = "route_path", columnDefinition = "geography(LineString,4326)")
    private LineString routePath;

    public Route() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public List<String> getMajorStops() { return majorStops; }
    public void setMajorStops(List<String> majorStops) { this.majorStops = majorStops; }
    public LineString getRoutePath() { return routePath; }
    public void setRoutePath(LineString routePath) { this.routePath = routePath; }
}
