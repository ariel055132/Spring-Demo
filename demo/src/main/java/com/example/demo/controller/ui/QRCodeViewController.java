package com.example.demo.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View Controller for QR Code web UI
 * Serves the HTML interface for QR code generation and management
 */
@Controller
public class QRCodeViewController {

    /**
     * Home page
     * 
     * @return The index.html template
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Display the QR code generation and management web interface
     * 
     * @return The qrcode.html template
     */
    @GetMapping("/qrcode/ui")
    public String qrcodeUI() {
        return "qrcode";
    }
    
    /**
     * Alias for easier access
     * 
     * @return The qrcode.html template
     */
    @GetMapping("/qrcode")
    public String qrcode() {
        return "qrcode";
    }
}
