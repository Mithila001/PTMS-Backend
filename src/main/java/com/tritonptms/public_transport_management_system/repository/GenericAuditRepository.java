package com.tritonptms.public_transport_management_system.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class GenericAuditRepository {

    private final EntityManager entityManager;

    @Autowired
    public GenericAuditRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> List<Object[]> getRevisionsForEntity(Class<T> entityClass, Serializable entityId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        return (List<Object[]>) auditReader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(entityId))
                .getResultList();
    }
}