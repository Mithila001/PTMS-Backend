package com.tritonptms.public_transport_management_system.repository;

import com.tritonptms.public_transport_management_system.model.ActionLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    List<ActionLog> findByEntityTypeOrderByTimestampDesc(String entityType);

    List<ActionLog> findByUsernameAndEntityTypeOrderByTimestampDesc(String username, String entityType);
}