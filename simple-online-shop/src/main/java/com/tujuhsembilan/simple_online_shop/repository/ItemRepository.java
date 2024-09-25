package com.tujuhsembilan.simple_online_shop.repository;

import com.tujuhsembilan.simple_online_shop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByItemId(Long itemId);

    Page<Item> findByItemNameContainingIgnoreCase(String itemName, Pageable pageable);

}
