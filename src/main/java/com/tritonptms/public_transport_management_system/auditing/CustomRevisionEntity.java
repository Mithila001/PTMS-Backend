package com.tritonptms.public_transport_management_system.auditing;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.ModifiedEntityNames;
import java.util.Set;

@Entity
@Table(name = "custom_revision_entity")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {

    private String username;

    @ElementCollection
    @CollectionTable(name = "rev_modified_entities", joinColumns = @JoinColumn(name = "rev"))
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }
}