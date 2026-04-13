package com.example.demo.service.qrcode.checker;

import org.springframework.stereotype.Component;

import com.example.demo.service.qrcode.arg.CreateQRCodeArg;

@Component
public class CreateQRCodeChecker extends QRCodeChecker<CreateQRCodeArg> {

    @Override
    protected void doCheckInternal(CreateQRCodeArg arg) {
        if (isQRCodeExistByUserIdAndOriginalUrl(arg.getUserId(), arg.getUrl())) {
            String message = QRCodeEnum.CREATE_DUPLICATE.getMessageTemplate();
            throw new IllegalStateException(message);
        }
    }
}
