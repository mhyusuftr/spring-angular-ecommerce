package com.tujuhsembilan.simple_online_shop.repository;

import com.tujuhsembilan.simple_online_shop.model.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerId(Long customerId);

    Page<Customer> findByCustomerNameContainingIgnoreCase(String customerName, Pageable pageable);
}
