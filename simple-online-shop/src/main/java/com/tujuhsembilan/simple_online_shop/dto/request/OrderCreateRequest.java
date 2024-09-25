package com.tujuhsembilan.simple_online_shop.dto.request;

import lombok.Data;

@Data
public class OrderCreateRequest {
    private Long customerId;
    private Long itemId;
    private Integer quantity;
}
