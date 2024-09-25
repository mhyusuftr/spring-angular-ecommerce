package com.tujuhsembilan.simple_online_shop.service;

import com.tujuhsembilan.simple_online_shop.configuration.Helpers;
import com.tujuhsembilan.simple_online_shop.dto.request.CustomerCreateRequest;
import com.tujuhsembilan.simple_online_shop.dto.request.CustomerEditRequest;
import com.tujuhsembilan.simple_online_shop.model.Customer;
import com.tujuhsembilan.simple_online_shop.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;
import java.util.Date;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MinioClient minio;

    @Value("${application.minio.bucketName}")
    private String bucketName;

    public Page<Customer> getAllCustomer(int page, int size, String sortBy, String direction, String customerName) throws Exception {

        try {
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Customer> activeCustomerPage;
            if (customerName == null || customerName.isEmpty()) {
                activeCustomerPage = customerRepository.findAll(pageable);
            } else {
                activeCustomerPage = customerRepository.findByCustomerNameContainingIgnoreCase(customerName, pageable);
            }

            return activeCustomerPage.map(this::setPicUrl);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Customer getCustomerById(Long customerId) throws Exception {
        try {
            Customer customer = customerRepository.findById(customerId).orElse(null);

            if(customer != null){
                customer = setPicUrl(customer);
            }

            return customer;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private Customer setPicUrl(Customer customer) {
        String pic = customer.getPic();
        if (pic != null) {
            String picUrl = Helpers.MINIO_URL + bucketName + "/" + pic;
            customer.setPic(picUrl);
        }
        return customer;
    }

    public Customer createCustomer(CustomerCreateRequest request, MultipartFile pic) throws Exception {

        try {
            long timestamp = new Date().getTime();

            Customer newCustomer = new Customer();
            newCustomer.setCustomerName(request.getCustomerName());
            newCustomer.setCustomerPhone(request.getCustomerPhone());
            newCustomer.setCustomerAddress(request.getCustomerAddress());
            newCustomer.setIsActive(true);

            if (pic != null) {
                String originalFilename = pic.getOriginalFilename();
                String[] fileNameParts = originalFilename.split("\\.");
                String fileName = fileNameParts[0] + "." + timestamp + "." + fileNameParts[1];

                try (InputStream inputStream = pic.getInputStream()) {
                    minio.putObject(
                        PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, pic.getSize(), -1)
                            .contentType(pic.getContentType())
                            .build()
                    );
                }

                newCustomer.setPic(fileName);
            }

            Customer savedCustomer = customerRepository.save(newCustomer);

            savedCustomer.setCustomerCode(Helpers.Modules.CUSTOMER_PREFIX + Helpers.DASH + savedCustomer.getCustomerId());

            return customerRepository.save(savedCustomer);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Customer updateCustomerById(Long customerId, CustomerEditRequest request, MultipartFile pic) throws Exception {

        try {
            Optional<Customer> customerOptional = customerRepository.findById(customerId);

            if(customerOptional.isEmpty()){
                return null;
            }

            Customer customer = customerOptional.get();

            customer.setCustomerName(request.getCustomerName());
            customer.setCustomerAddress(request.getCustomerAddress());
            customer.setCustomerPhone(request.getCustomerPhone());
            customer.setIsActive(request.getIsActive());

            if(pic != null){
                // if there's a new image, delete the old one
                if (customer.getPic() != null) {
                    try {
                        minio.removeObject(
                            RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(customer.getPic())
                                .build()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(Helpers.ResponseMessage.MINIO_DEL_ERROR, e);
                    }
                }

                long timestamp = new Date().getTime();
                String originalFilename = pic.getOriginalFilename();
                String[] fileNameParts = originalFilename.split("\\.");
                String fileName = fileNameParts[0] + "." + timestamp + "." + fileNameParts[1];

                // insert the new image
                try (InputStream inputStream = pic.getInputStream()) {
                    minio.putObject(
                        PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, pic.getSize(), -1)
                            .contentType(pic.getContentType())
                            .build()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(Helpers.ResponseMessage.MINIO_UPL_ERROR, e);
                }

                customer.setPic(fileName);
            }

            return customerRepository.save(customer);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public Customer deleteCustomerById(Long customerId) throws Exception {
        try {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if(customer != null){
                customerRepository.delete(customer);
            }
            return customer;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
