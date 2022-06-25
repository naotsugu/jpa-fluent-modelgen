package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.data.jpa.domain.Specification;
import static com.mammb.code.jpa.fluent.modelgen.example.CustomerModel.root;

public class CustomerSpecs {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, cb) -> root(root, query, cb).getFirstName().like(firstName);
    }

    public static Specification<Customer> lastNameLike(String lastName) {
        return (root, query, cb) -> root(root, query, cb).getLastName().likePartial(lastName);
    }

    public static Specification<Customer> organizationNameLike(String name) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root(root, query, cb).joinOrganizations().getName().like(name);
        };
    }

    public static Specification<Customer> organizationZipEq(String zipCode) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root(root, query, cb).joinOrganizations().getAddress().getZipCode().getCode().eq(zipCode);
        };
    }

}
