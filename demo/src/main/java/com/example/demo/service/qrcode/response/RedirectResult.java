package com.example.demo.service.qrcode.response;

/**
 * Result of a redirect resolution — carries the URL (if found) and a status
 * indicating whether to 302-redirect, 404, or 410.
 * Matches the redirect flow from the qr_code_generator reference implementation.
 */
public record RedirectResult(String url, RedirectStatus status) {

    public enum RedirectStatus {
        FOUND,
        NOT_FOUND,
        GONE
    }

    public static RedirectResult found(String url) {
        return new RedirectResult(url, RedirectStatus.FOUND);
    }

    public static RedirectResult notFound() {
        return new RedirectResult(null, RedirectStatus.NOT_FOUND);
    }

    public static RedirectResult gone() {
        return new RedirectResult(null, RedirectStatus.GONE);
    }
}
