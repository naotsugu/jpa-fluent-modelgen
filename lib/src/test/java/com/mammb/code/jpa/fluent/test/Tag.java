package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Entity;

@Entity
public class Tag extends BaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
