package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}