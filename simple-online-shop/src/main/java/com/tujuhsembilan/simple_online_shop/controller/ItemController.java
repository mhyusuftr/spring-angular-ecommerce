package com.tujuhsembilan.simple_online_shop.controller;

import com.tujuhsembilan.simple_online_shop.configuration.Helpers;
import com.tujuhsembilan.simple_online_shop.dto.request.ItemCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.ItemEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Item;
import com.tujuhsembilan.simple_online_shop.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<Object> getAllItem(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = Helpers.PAGE_SIZE_DEFAULT) int size,
        @RequestParam(defaultValue = "itemName") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String itemName) throws Exception {

        Page<Item> itemPage = itemService.getAllItem(page, size, sortBy, direction, itemName);

        if (itemPage.isEmpty()) {
            throw new Exception(String.format(Helpers.ResponseMessage.EMPTY_DATA, Helpers.Modules.ITEM));
        }

        return ResponseEntity.ok(itemPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) throws Exception {
        try {
            Item item = itemService.getItemById(id);
            if(item == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, id.toString()));
            }
            return ResponseEntity.ok(item);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemCreateRequest request) throws Exception {
        try {
            Item createdItem = itemService.createItem(request);
            return ResponseEntity.ok(createdItem);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateItemById(@PathVariable Long id, @RequestBody ItemEditRequest request) throws Exception {
        try {

            Item item = itemService.updateItemById(id, request);
            if(item == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, id.toString()));
            }
            return ResponseEntity.ok(item);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItemById(@PathVariable Long id) throws Exception {
        try {

            Item deletedItem = itemService.deleteItemById(id);
            if(deletedItem == null){
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, id.toString()));
            }
            return ResponseEntity.ok(String.format(Helpers.ResponseMessage.DELETE_SUCCESS, Helpers.Modules.ITEM, id.toString()));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
