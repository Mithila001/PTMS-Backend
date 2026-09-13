package com.tritonptms.ptms.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tritonptms.ptms.route.Route;
import com.tritonptms.ptms.route.RouteRepository;
import com.tritonptms.ptms.user.ERole;
import com.tritonptms.ptms.user.Role;
import com.tritonptms.ptms.user.RoleRepository;
import com.tritonptms.ptms.user.User;
import com.tritonptms.ptms.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class PtmsApplicationIntegrationTest {

        private static final DockerImageName POSTGIS_IMAGE = DockerImageName
                        .parse("postgis/postgis:16-3.4-alpine")
                        .asCompatibleSubstituteFor("postgres");

        @Container
        static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGIS_IMAGE)
                        .withDatabaseName("ptms_test")
                        .withUsername("ptms")
                        .withPassword("ptms");

        @DynamicPropertySource
        static void databaseProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
                registry.add("spring.datasource.username", POSTGRES::getUsername);
                registry.add("spring.datasource.password", POSTGRES::getPassword);
                registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        }

        @Autowired
        MockMvc mockMvc;
        @Autowired
        ObjectMapper objectMapper;
        @Autowired
        UserRepository userRepository;
        @Autowired
        RoleRepository roleRepository;
        @Autowired
        RouteRepository routeRepository;
        @Autowired
        PasswordEncoder passwordEncoder;
        @Autowired
        EntityManager entityManager;
        @Autowired
        Flyway flyway;

        @BeforeEach
        void ensureLoginUser() {
                if (userRepository.existsByUsername("testadmin")) {
                        return;
                }
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN.name()).orElseThrow();
                User user = new User();
                user.setUsername("testadmin");
                user.setPassword(passwordEncoder.encode("Admin123!"));
                user.setEmail("testadmin@ptms.local");
                user.setFirstName("Test");
                user.setLastName("Admin");
                user.setNic("199999999999");
                user.setRoles(new HashSet<>(Set.of(adminRole)));
                userRepository.saveAndFlush(user);
        }

        @Test
        void flywayBuildsCompleteSchemaAndHealthIsUp() throws Exception {
                assertThat(flyway.info().current()).isNotNull();
                assertThat(flyway.info().current().getVersion().toString()).isEqualTo("3");

                mockMvc.perform(get("/actuator/health"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("UP"));
        }

        @Test
        void postgisLineStringRoundTripsThroughRepository() {
                String routeNumber = "IT-" + UUID.randomUUID().toString().substring(0, 8);
                GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
                LineString path = factory.createLineString(new Coordinate[] {
                                new Coordinate(79.8612, 6.9271),
                                new Coordinate(79.8899, 6.9147)
                });

                Route route = new Route();
                route.setRouteNumber(routeNumber);
                route.setOrigin("Colombo");
                route.setDestination("Battaramulla");
                route.setMajorStops(List.of("Borella"));
                route.setRoutePath(path);
                Long id = routeRepository.saveAndFlush(route).getId();

                entityManager.clear();
                Route reloaded = routeRepository.findById(id).orElseThrow();

                assertThat(reloaded.getRoutePath()).isNotNull();
                assertThat(reloaded.getRoutePath().getSRID()).isEqualTo(4326);
                assertThat(reloaded.getRoutePath().getNumPoints()).isEqualTo(2);
                assertThat(reloaded.getCreatedAt()).isNotNull();
                assertThat(reloaded.getCreatedBy()).isEqualTo("system");
        }

        @Test
        void unauthenticatedApiRequestReturnsProblemDetail401() throws Exception {
                mockMvc.perform(get("/api/buses"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                                .andExpect(jsonPath("$.status").value(401))
                                .andExpect(jsonPath("$.type").value("urn:problem:unauthorized"));
        }

        @Test
        void csrfIsRequiredForStateChangingRequests() throws Exception {
                mockMvc.perform(post("/api/buses")
                                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                                .user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validBusJson("ND-CSRF", 4100000001L)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @WithMockUser(username = "normal-user", roles = "USER")
        void normalUserCannotCreateBusEvenWithCsrf() throws Exception {
                mockMvc.perform(post("/api/buses")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validBusJson("ND-FORBID", 4200000001L)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void validationAndNotFoundErrorsUseProblemDetails() throws Exception {
                mockMvc.perform(post("/api/buses")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.type").value("urn:problem:validation-error"))
                                .andExpect(jsonPath("$.errors.registrationNumber").exists());

                mockMvc.perform(get("/api/buses/999999999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.type").value("urn:problem:resource-not-found"));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void duplicateBusReturns409() throws Exception {
                String registration = "ND-DUP-" + UUID.randomUUID().toString().substring(0, 4);
                long permit = 4300000001L;
                String body = validBusJson(registration, permit);

                mockMvc.perform(post("/api/buses").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/api/buses").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void busSearchUsesSpecificationAndPagination() throws Exception {
                String marker = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                String matchingRegistration = "SP-" + marker;

                mockMvc.perform(post("/api/buses").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validBusJson(matchingRegistration, 4350000001L)))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/buses/search")
                                .param("registrationNumber", marker)
                                .param("serviceType", "PRIVATE_BUS")
                                .param("page", "0")
                                .param("size", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].registrationNumber").value(matchingRegistration))
                                .andExpect(jsonPath("$.page.totalElements").value(1));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void busChangesProduceSpringDataEnversRevisions() throws Exception {
                String registration = "ND-AUD-" + UUID.randomUUID().toString().substring(0, 4);
                var createResult = mockMvc.perform(post("/api/buses").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validBusJson(registration, 4400000001L)))
                                .andExpect(status().isCreated())
                                .andReturn();
                long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

                String updated = validBusJson(registration, 4400000001L).replace("\"model\":\"Viking\"",
                                "\"model\":\"Viking X\"");
                mockMvc.perform(put("/api/buses/{id}", id).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(updated))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.model").value("Viking X"));

                mockMvc.perform(get("/api/buses/{id}/revisions", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].revision").isNumber())
                                .andExpect(jsonPath("$[0].modifiedBy").value("admin"))
                                .andExpect(jsonPath("$[0].snapshot.registrationNumber").value(registration))
                                .andExpect(jsonPath("$[1].snapshot.model").value("Viking X"));
        }

        @Test
        void jsonLoginCreatesSessionAndLogoutEndsIt() throws Exception {
                var login = mockMvc.perform(post("/api/auth/login").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testadmin\",\"password\":\"Admin123!\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("testadmin"))
                                .andReturn();

                MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
                assertThat(session).isNotNull();

                mockMvc.perform(get("/api/auth/me").session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("testadmin"));

                mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/auth/me"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void invalidLoginReturnsStandard401Problem() throws Exception {
                mockMvc.perform(post("/api/auth/login").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testadmin\",\"password\":\"wrong\"}"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.status").value(401))
                                .andExpect(jsonPath("$.type").value("urn:problem:invalid-credentials"));
        }

        @Test
        void csrfEndpointIsPublic() throws Exception {
                mockMvc.perform(get("/api/auth/csrf"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
                                .andExpect(jsonPath("$.token").isNotEmpty());
        }

        private String validBusJson(String registrationNumber, long permitNumber) throws Exception {
                JsonNode node = objectMapper.readTree("""
                                {
                                  "registrationNumber": "ND-TEST",
                                  "make": "Ashok Leyland",
                                  "model": "Viking",
                                  "yearOfManufacture": 2024,
                                  "fuelType": "DIESEL",
                                  "active": true,
                                  "seatingCapacity": 45,
                                  "standingCapacity": 15,
                                  "ntcPermitNumber": 4000000001,
                                  "comfortType": "NORMAL",
                                  "airConditioned": false,
                                  "serviceType": "PRIVATE_BUS"
                                }
                                """);
                ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("registrationNumber", registrationNumber);
                ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("ntcPermitNumber", permitNumber);
                return objectMapper.writeValueAsString(node);
        }
}
