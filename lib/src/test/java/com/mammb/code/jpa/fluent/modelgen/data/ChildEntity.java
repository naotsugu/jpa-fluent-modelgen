package com.mammb.code.jpa.fluent.modelgen.data;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class ChildEntity extends SuperEntity {

    private String name;

    @Embedded
    private ValueObject valueObject;

}
