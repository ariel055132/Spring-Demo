package com.example.demo.service.qrcode.checker;

import com.example.foundation.checker.CheckMessage;

public enum QRCodeEnum implements CheckMessage {
    CREATE_DUPLICATE("QR code already exists"),
    UPDATE_NOT_FOUND("QR code not found. Cannot update"),
    DELETE_NOT_FOUND("QR code not found. Cannot delete")
    ;
    
    private final String messageTemplate;

    QRCodeEnum(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
    
}
