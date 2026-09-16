package com.fjs.algacomments.comments_service.domain.model.identifier;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.util.UUID;

@MappedSuperclass
public abstract class UUIDv7Entity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @PrePersist
    protected void generateId() {
        if (id == null) {
            id = UUIDv7Generator.next();
        }
    }

    public UUID getId() {
        return id;
    }
}
