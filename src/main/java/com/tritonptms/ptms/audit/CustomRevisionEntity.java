package com.tritonptms.ptms.audit;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.util.Set;

@Entity
@Table(name = "custom_revision_entity")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(nullable = false)
    private long timestamp;

    @Column(length = 100)
    private String username;

    @ElementCollection
    @CollectionTable(name = "rev_modified_entities", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entity_name", nullable = false, length = 255)
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Set<String> getModifiedEntityNames() { return modifiedEntityNames; }
    public void setModifiedEntityNames(Set<String> modifiedEntityNames) { this.modifiedEntityNames = modifiedEntityNames; }
}
