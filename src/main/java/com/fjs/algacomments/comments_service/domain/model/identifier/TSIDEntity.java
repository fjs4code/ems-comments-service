package com.fjs.algacomments.comments_service.domain.model.identifier;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class TSIDEntity {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @PrePersist
    protected void generateId() {
        if (id == null) {
            id = TSIDGenerator.nextLong();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTSID() {
        return id == null
                ? null
                : TSIDCodec.encode(id);
    }
}
