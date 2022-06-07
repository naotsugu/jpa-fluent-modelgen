package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Entity;

@Entity
public class SpecialTag extends Tag {
    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
