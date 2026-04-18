package com.example.demo.service.qrcode.checker;

import com.example.demo.service.qrcode.arg.DeleteQRCodeArg;
import org.springframework.stereotype.Component;

@Component
public class DeleteQRCodeChecker extends QRCodeChecker<DeleteQRCodeArg> {

    @Override
    protected void doCheckInternal(DeleteQRCodeArg arg) {
        if (!isQRCodeExist(arg.getShortCode(), arg.getUserId())) {
            throw new IllegalArgumentException("QR code not found");
        }
    }
}

