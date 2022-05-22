package com.mammb.code.jpa.fluent.modelgen.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @GetMapping("/customer")
    public List<Customer> get() {
        return repository.findAll();
    }

}
