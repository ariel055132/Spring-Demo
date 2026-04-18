package com.example.demo.service.qrcode.arg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteQRCodeArg {
    private String shortCode;
    private String userId;
}
