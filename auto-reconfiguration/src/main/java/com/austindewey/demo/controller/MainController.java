package com.austindewey.demo.controller;

import java.util.List;

import com.austindewey.demo.model.Customer;
import com.austindewey.demo.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping("/")
    public String index() {
        return "Hello World!";
    }

    @RequestMapping(value="/save", method = RequestMethod.POST)
    public HttpStatus insertCustomer(@RequestBody Customer customer) {
        boolean status = customerRepository.save(customer) != null;
        return status ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
    }

    @RequestMapping("/findall")
    public List<Customer> findAll() {
        return (List<Customer>) customerRepository.findAll();
    }
}