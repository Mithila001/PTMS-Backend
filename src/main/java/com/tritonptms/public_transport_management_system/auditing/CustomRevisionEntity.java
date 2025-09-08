package com.tritonptms.public_transport_management_system.auditing;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.ModifiedEntityNames;
import java.util.Set;

@Entity
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {

    @ElementCollection
    @CollectionTable(name = "rev_modified_entities", joinColumns = @JoinColumn(name = "rev"))
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;

    // Getters and Setters
    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }
}