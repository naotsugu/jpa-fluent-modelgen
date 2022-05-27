package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import java.util.regex.Pattern;

public class CustomerSpecs {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, cb) -> cb.like(on(root).getFirstName(), partial(firstName), '\\');
    }

    public static Specification<Customer> lastNameLike(String lastName) {
        return (root, query, cb) -> cb.like(on(root).getLastName(), partial(lastName), '\\');
    }

    public static Specification<Customer> organizationNameLike(String name) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.like(on(root).joinOrganizations().getName(), partial(name), '\\');
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

    static Pattern ESCAPE_PATTERN = Pattern.compile("([%_\\\\])");
    static String partial(String str) {
        return "%" + ESCAPE_PATTERN.matcher(str).replaceAll("\\\\$1") + "%";
    }
}
