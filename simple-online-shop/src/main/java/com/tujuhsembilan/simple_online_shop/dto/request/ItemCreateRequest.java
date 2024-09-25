package com.tujuhsembilan.simple_online_shop.dto.request;

import lombok.Data;

@Data
public class ItemCreateRequest {
    private String itemName;
    private String itemCode;
    private Integer stock;
    private Integer price;
    private Boolean isAvailable;
}
