package com.tujuhsembilan.simple_online_shop.service;
import java.util.Date;
import java.util.Optional;

import com.tujuhsembilan.simple_online_shop.dto.request.ItemCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.ItemEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Item;
import com.tujuhsembilan.simple_online_shop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Page<Item> getAllItem(int page, int size, String sortBy, String direction, String itemName) throws Exception {

        try {
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Item> availableItemPage;
            if (itemName == null || itemName.isEmpty()) {
                availableItemPage = itemRepository.findAll(pageable);
            } else {
                availableItemPage = itemRepository.findByItemNameContainingIgnoreCase(itemName, pageable);
            }

            return availableItemPage;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Item getItemById(Long itemId) throws Exception {

        try {
            return itemRepository.findByItemId(itemId).orElse(null);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Item createItem(ItemCreateRequest request) throws Exception {

        try {
            Item newItem = new Item();

            newItem.setItemName(request.getItemName());
            newItem.setItemCode(request.getItemCode());
            newItem.setStock(request.getStock());
            newItem.setPrice(request.getPrice());
            newItem.setIsAvailable(true);
            newItem.setLastReStock(new Date());

            return itemRepository.save(newItem);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Item updateItemById(Long itemId, ItemEditRequest request) throws Exception {

        try {
            Optional<Item> itemOptional = itemRepository.findById(itemId);

            if (itemOptional.isPresent()) {

                Item item = itemOptional.get();
                item.setItemName(request.getItemName());
                item.setItemCode(request.getItemCode());
                item.setStock(request.getStock());
                item.setPrice(request.getPrice());
                item.setIsAvailable(request.getIsAvailable());
                item.setLastReStock(request.getLastRestock());

                return itemRepository.save(item);
            } else {
                return null;
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Item deleteItemById(Long itemId) throws Exception {
        try {
            Item item = itemRepository.findById(itemId).orElse(null);
            if(item != null){
                itemRepository.delete(item);
            }
            return item;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
