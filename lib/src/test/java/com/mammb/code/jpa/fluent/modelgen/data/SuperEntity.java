package com.mammb.code.jpa.fluent.modelgen.data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

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
