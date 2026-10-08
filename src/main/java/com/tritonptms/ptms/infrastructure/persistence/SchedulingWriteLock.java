package com.tritonptms.ptms.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class SchedulingWriteLock {

    @PersistenceContext
    private EntityManager entityManager;

    public void acquire() {
        // ponytail: one lock serializes scheduling writes; use per-resource locks if booking throughput matters.
        entityManager.createNativeQuery("SELECT pg_advisory_xact_lock(48271, 1) IS NULL").getSingleResult();
    }
}
