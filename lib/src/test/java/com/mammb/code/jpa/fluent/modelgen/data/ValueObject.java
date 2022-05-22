package com.mammb.code.jpa.fluent.modelgen.data;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public class ValueObject implements Serializable {

    private String name;

    private LocalDate localDate;

}
