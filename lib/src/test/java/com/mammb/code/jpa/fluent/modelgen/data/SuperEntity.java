package com.mammb.code.jpa.fluent.modelgen.data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.io.Serializable;

@MappedSuperclass
public class SuperEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }
}
