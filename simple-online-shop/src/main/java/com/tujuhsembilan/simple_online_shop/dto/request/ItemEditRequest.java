package com.tujuhsembilan.simple_online_shop.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class ItemEditRequest {
    private String itemName;
    private String itemCode;
    private Integer stock;
    private Integer price;
    private Boolean isAvailable;
    private Date lastRestock;
}
