package com.tujuhsembilan.simple_online_shop.controller;

import com.tujuhsembilan.simple_online_shop.configuration.Helpers;
import com.tujuhsembilan.simple_online_shop.dto.request.CustomerCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.CustomerEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Customer;
import com.tujuhsembilan.simple_online_shop.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<Object> getAllCustomer(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = Helpers.PAGE_SIZE_DEFAULT) int size,
        @RequestParam(defaultValue = "customerName") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String customerName) throws Exception {

        try {

            Page<Customer> customerPage = customerService.getAllCustomer(page, size, sortBy, direction, customerName);
            if (customerPage.isEmpty()) {
                throw new Exception(String.format(Helpers.ResponseMessage.EMPTY_DATA, Helpers.Modules.CUSTOMER));
            }
            return ResponseEntity.ok(customerPage);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable Long id) throws Exception {

        try {

            Customer customer = customerService.getCustomerById(id);
            if(customer == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, id.toString()));
            }
            return ResponseEntity.ok(customer);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestPart("customer") CustomerCreateRequest request, @RequestPart(name = "pic", required = false) MultipartFile pic) throws Exception {
        try {
            Customer createdCustomer = customerService.createCustomer(request, pic);
            return ResponseEntity.ok(createdCustomer);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCustomerById(@PathVariable Long id, @RequestPart("customer") CustomerEditRequest request, @RequestPart(name = "pic", required = false) MultipartFile pic) throws Exception {
        try {

            Customer customer = customerService.updateCustomerById(id, request, pic);
            if(customer == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, id.toString()));
            }
            return ResponseEntity.ok(customer);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable Long id) throws Exception {
        try {

            Customer deletedCustomer = customerService.deleteCustomerById(id);
            if(deletedCustomer == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, id.toString()));
            }
            return ResponseEntity.ok(String.format(Helpers.ResponseMessage.DELETE_SUCCESS, Helpers.Modules.ITEM, id.toString()));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
