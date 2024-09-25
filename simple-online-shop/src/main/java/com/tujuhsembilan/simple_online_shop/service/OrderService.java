package com.tujuhsembilan.simple_online_shop.service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import com.tujuhsembilan.simple_online_shop.configuration.Helpers;
import com.tujuhsembilan.simple_online_shop.dto.request.OrderCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.OrderEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Customer;
import com.tujuhsembilan.simple_online_shop.model.Item;
import com.tujuhsembilan.simple_online_shop.model.Order;
import com.tujuhsembilan.simple_online_shop.repository.CustomerRepository;
import com.tujuhsembilan.simple_online_shop.repository.ItemRepository;
import com.tujuhsembilan.simple_online_shop.repository.OrderRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Page<Order> getAllOrder(int page, int size, String sortBy, String direction, String customerName) throws Exception {

        try {
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Order> orderPage;
            if (customerName == null || customerName.isEmpty()) {
                orderPage = orderRepository.findAll(pageable);
            } else {
                orderPage = orderRepository.findByCustomerCustomerNameContainingIgnoreCase(customerName, pageable);
            }

            return orderPage;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Order getOrderById(Long orderId) throws Exception {

        try {
            return orderRepository.findByOrderId(orderId).orElse(null);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Order createOrder(OrderCreateRequest request) throws Exception {

        try {
            Item item = itemRepository.findById(request.getItemId()).orElse(null);
            Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);

            Order newOrder = new Order();
            newOrder.setOrderDate(new Date());
            newOrder.setQuantity(request.getQuantity());

            if(item != null){

                // check whether the item is available or not
                if((item.getIsAvailable() != null) && (!item.getIsAvailable())){
                    throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, request.getItemId().toString()));
                }

                Integer totalPrice = item.getPrice() * newOrder.getQuantity();
                Integer updatedStock = item.getStock() - newOrder.getQuantity();
                item.setStock(updatedStock);

                itemRepository.save(item);

                newOrder.setItem(item);
                newOrder.setTotalPrice(totalPrice);
                newOrder.getItem().setStock(updatedStock);

            }
            else{
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, request.getItemId().toString()));
            }

            if(customer != null){
                if((customer.getIsActive() != null) && (!customer.getIsActive())){
                    throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, request.getItemId().toString()));
                }

                customer.setLastOrderDate(new Date());

                customerRepository.save(customer);

                newOrder.setCustomer(customer);
            }
            else{
                throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, request.getCustomerId().toString()));
            }
            Format formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = formatter.format(new Date());

            String orderCode = Helpers.Modules.ORDER_PREFIX + Helpers.DASH  + timestamp;

            newOrder.setOrderCode(orderCode);

            return orderRepository.save(newOrder);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Order updateOrderById(Long id, OrderEditRequest request) throws Exception {

        try {
            Order order = orderRepository.findById(id).orElse(null);

            if (order != null) {

                Integer oldQuantity = order.getQuantity();
                Item oldItem = order.getItem();
                Integer stock = 0;

                order.setQuantity(request.getQuantity());

                Item newItem = itemRepository.findById(request.getItemId()).orElse(null);

                if((newItem != null)){

                    if((newItem.getIsAvailable() != null) && (!newItem.getIsAvailable())){
                        throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, request.getItemId().toString()));
                    }

                    if((oldItem != null) && (!oldItem.getItemId().equals(newItem.getItemId()))){
                        stock = oldItem.getStock() + oldQuantity;
                        oldItem.setStock(stock);
                        itemRepository.save(oldItem);
                        oldQuantity = 0;
                    }

                    stock = newItem.getStock() + oldQuantity - order.getQuantity();
                    newItem.setStock(stock);
                    itemRepository.save(newItem);

                    Integer totalPrice = newItem.getPrice() * order.getQuantity();

                    order.setItem(newItem);
                    order.setTotalPrice(totalPrice);

                }
                else{
                    throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.ITEM, request.getItemId().toString()));
                }

                Customer newCustomer = customerRepository.findById(request.getCustomerId()).orElse(null);

                if(newCustomer != null){

                    if((newCustomer.getIsActive() != null) && (!newCustomer.getIsActive())){
                        throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, request.getItemId().toString()));
                    }

                    newCustomer.setLastOrderDate(new Date());

                    customerRepository.save(newCustomer);

                    order.setCustomer(newCustomer);
                }
                else{
                    throw new Exception(String.format(Helpers.ResponseMessage.NOT_FOUND, Helpers.Modules.CUSTOMER, request.getCustomerId().toString()));
                }

                return orderRepository.save(order);
            } else {
                return null;
            }

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Order deleteOrderById(Long id) throws Exception {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                Item item = order.getItem();
                if (item != null) {
                    Integer oldQuantity = order.getQuantity() != null ? order.getQuantity() : 0;
                    item.setStock(item.getStock() + oldQuantity);
                    itemRepository.save(item);
                }
                orderRepository.deleteById(id);
            }
            return order;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity<Object> generateReport() throws Exception {
        try {
            List<Order> orderList = orderRepository.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orderList);

            File file = ResourceUtils.getFile("classpath:report/OrderReport.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "SYSTEM");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "OrderReport.pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public boolean checkUsedId(String module, long id) throws Exception {
        return switch (module) {
            case Helpers.Modules.ITEM -> orderRepository.findFirstByItemItemId(id).isPresent();
            case Helpers.Modules.CUSTOMER -> orderRepository.findFirstByCustomerCustomerId(id).isPresent();
            default -> throw new Exception("Module not valid");
        };
    }
}
