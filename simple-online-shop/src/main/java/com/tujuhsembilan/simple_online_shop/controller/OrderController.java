package com.tujuhsembilan.simple_online_shop.controller;
import com.tujuhsembilan.simple_online_shop.configuration.Helpers;
import com.tujuhsembilan.simple_online_shop.dto.request.OrderCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.OrderEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Order;
import com.tujuhsembilan.simple_online_shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Object> getAllOrder(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = Helpers.PAGE_SIZE_DEFAULT) int size,
        @RequestParam(defaultValue = "orderDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String customerName) throws Exception {

        try {
            Page<Order> orderPage = orderService.getAllOrder(page, size, sortBy, direction, customerName);

            if (orderPage.isEmpty()) {
                throw new Exception(String.format(Helpers.ResponseMessage.EMPTY_DATA, Helpers.Modules.ORDER));
            }

            return ResponseEntity.ok(orderPage);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id) throws Exception {
        try {

            Order order = orderService.getOrderById(id);
            if (order == null) {
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ORDER, id.toString()));
            }
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrderCreateRequest request) throws Exception {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateOrderById(@PathVariable Long id, @RequestBody OrderEditRequest request) throws Exception {
        try {

            Order order = orderService.updateOrderById(id, request);
            if (order == null) {
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ORDER, id.toString()));
            }
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Long id) throws Exception {
        try {
            Order deletedOrder = orderService.deleteOrderById(id);
            if (deletedOrder == null) {
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ORDER, id.toString()));
            }
            return ResponseEntity.ok(String.format(Helpers.ResponseMessage.DELETE_SUCCESS, Helpers.Modules.ORDER, id.toString()));

        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/report")
    public ResponseEntity<Object> generateReport() throws Exception {

        try {
            return orderService.generateReport();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @GetMapping("/checkUsedId")
    public ResponseEntity<Object> checkUsedId(@RequestParam("module") String module, @RequestParam("id") long id) throws Exception {

        try {
            boolean isUsed = orderService.checkUsedId(module, id);
            return ResponseEntity.ok(isUsed);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}
