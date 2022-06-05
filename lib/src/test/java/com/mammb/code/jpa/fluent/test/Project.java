package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

@Entity
public class Project extends BaseEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectState state;

    @Embedded
    private Duration duration;

    @ManyToOne
    private Project parent;

}
