package com.mammb.code.jpa.fluent.test;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public class Duration implements Serializable {

    public LocalDate open;
    public LocalDate close;

    public LocalDate getOpen() {
        return open;
    }

    public void setOpen(LocalDate open) {
        this.open = open;
    }

    public LocalDate getClose() {
        return close;
    }

    public void setClose(LocalDate close) {
        this.close = close;
    }
}
