package com.example.demo.service.qrcode.checker;

import com.example.foundation.checker.CheckMessage;

public enum QRCodeEnum implements CheckMessage {
    CREATE_DUPLICATE("QRCode already exists"),
    UPDATE_NOT_FOUND("No qrcode is found. Cannot update"),
    DELETE_NOT_FOUND("No qrcode is found. Cannot delete")
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
