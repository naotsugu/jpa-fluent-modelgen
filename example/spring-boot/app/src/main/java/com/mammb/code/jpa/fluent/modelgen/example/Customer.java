package com.mammb.code.jpa.fluent.modelgen.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.io.Serializable;
import java.util.List;

@Entity
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToMany
    private List<Organization> organizations;


    protected Customer() {}

    public Customer(String firstName, String lastName, List<Organization> organizations) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizations = organizations;
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

}
