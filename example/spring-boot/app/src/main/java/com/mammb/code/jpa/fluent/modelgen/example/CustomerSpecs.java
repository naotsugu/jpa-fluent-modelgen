package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;

public class CustomerSpecs {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, cb) -> cb.like(on(root).getFirstName(), '%' + firstName + '%');
    }

    public static Specification<Customer> lastNameLike(String lastName) {
        return (root, query, cb) -> cb.like(on(root).getLastName(), '%' + lastName + '%');
    }

    public static Specification<Customer> organizationNameLike(String name) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.like(on(root).joinOrganizations().getName(), '%' + name + '%');
        };
    }

    public static Specification<Customer> organizationZipEq(String zipCode) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(on(root).joinOrganizations().getAddress().getZipCode().getCode(), zipCode);
        };
    }

    private static Customer_Root_ on(Root<Customer> root) {
        return new Customer_Root_(root);
    }

}
