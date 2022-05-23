package com.mammb.code.jpa.fluent.modelgen.example;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Organization> organizations;


    protected Customer() {}

    public Customer(String firstName, String lastName, List<Organization> organizations) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizations = organizations;
    }

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizations = new ArrayList<>();
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
