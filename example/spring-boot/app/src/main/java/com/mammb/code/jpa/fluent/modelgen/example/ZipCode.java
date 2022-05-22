package com.mammb.code.jpa.fluent.modelgen.example;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ZipCode implements Serializable {

    private String code;

    protected ZipCode() {}

    public ZipCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
