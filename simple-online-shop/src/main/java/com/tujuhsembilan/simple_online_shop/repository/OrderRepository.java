package com.tujuhsembilan.simple_online_shop.repository;

import com.tujuhsembilan.simple_online_shop.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(Long orderId);

    Page<Order> findByCustomerCustomerNameContainingIgnoreCase(String customerName, Pageable pageable);

    Optional<Order> findFirstByCustomerCustomerId(Long customerId);

    Optional<Order> findFirstByItemItemId(Long itemId);

}
