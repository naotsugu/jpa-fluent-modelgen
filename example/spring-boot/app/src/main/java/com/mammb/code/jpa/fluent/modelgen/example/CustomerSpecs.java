package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;

public class CustomerSpecs {

    public Specification<Customer> firstNameLike(String firstName) {
        return (root, query, cb) -> cb.like(on(root).getFirstName(), '%' + firstName + '%');
    }

    public Specification<Customer> lastNameLike(String lastName) {
        return (root, query, cb) -> cb.like(on(root).getLastName(), '%' + lastName + '%');
    }

    public Specification<Customer> organizationNameLike(String name) {
        return (root, query, cb) -> cb.like(on(root).joinOrganizations().getName(), '%' + name + '%');
    }

    public Specification<Customer> organizationZipEq(String zipCode) {
        return (root, query, cb) -> cb.equal(on(root).joinOrganizations().getAddress().getZipCode().getCode(), zipCode);
    }


    private Customer_Root_ on(Root<Customer> root) {
        return new Customer_Root_(root);
    }
}
