package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @GetMapping("/customers")
    public List<Customer> customers() {
        return repository.findAll();
    }

    @GetMapping("/customers/{id}")
    public Optional<Customer> get(@PathVariable long id) {
        return repository.findById(id);
    }

    @GetMapping("/customers/organization-name/{name}")
    public List<Customer> getByOrganName(@PathVariable String name) {
        return repository.findAll(CustomerSpecs.organizationNameLike(name));
    }

    @GetMapping("/customers/organization-zip/{code}")
    public List<Customer> getByOrganZip(@PathVariable String code) {
        return repository.findAll(CustomerSpecs.organizationZipEq(code));
    }

}
