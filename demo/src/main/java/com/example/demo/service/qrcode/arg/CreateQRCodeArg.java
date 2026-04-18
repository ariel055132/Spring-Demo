package com.example.demo.service.qrcode.arg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQRCodeArg {
    private String url;

    private String userId;

    private Integer width;

    private Integer height;
}
