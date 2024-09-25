package com.tujuhsembilan.simple_online_shop.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CustomerCreateRequest {
    private String customerName;
    private String customerPhone;
    private String customerAddress;
}
