package com.tujuhsembilan.simple_online_shop.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_order_date")
    private Date lastOrderDate;

    @Column(name = "pic")
    private String pic;

}
