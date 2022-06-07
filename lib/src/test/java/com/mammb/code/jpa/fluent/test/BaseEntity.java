package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    private LocalDateTime createdOn;

    private LocalDateTime lastModifiedOn;


    @PrePersist
    public void prePersistBase() {
        createdOn = LocalDateTime.now();
        lastModifiedOn = createdOn;
    }

    @PreUpdate
    public void preUpdateBase() {
        lastModifiedOn = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }
}
