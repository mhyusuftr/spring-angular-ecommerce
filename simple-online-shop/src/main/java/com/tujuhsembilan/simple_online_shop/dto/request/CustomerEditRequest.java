package com.tujuhsembilan.simple_online_shop.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CustomerEditRequest {
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private Boolean isActive;
}
