package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Entity;

@Entity
public class ExternalProject extends Project {

    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
