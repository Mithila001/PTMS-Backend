package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}