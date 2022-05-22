package com.mammb.code.jpa.fluent.modelgen.example;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.io.Serializable;

@Embeddable
public class Address implements Serializable {

    @Embedded
    private ZipCode zipCode;

    private String country;

    private String state;

    private String city;

    private String street;

    protected Address() {}

    public Address(ZipCode zipCode, String country, String state, String city, String street) {
        this.zipCode = zipCode;
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }
}
