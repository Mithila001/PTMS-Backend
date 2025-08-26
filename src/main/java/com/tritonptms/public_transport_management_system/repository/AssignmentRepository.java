package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}