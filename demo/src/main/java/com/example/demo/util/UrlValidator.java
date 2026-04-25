package com.example.demo.util;

import java.net.URI;
import java.util.Set;

public class UrlValidator {

    private static final int MAX_URL_LENGTH = 2048;

    private static final Set<String> BLOCKED_DOMAINS = Set.of(
            "evil.com",
            "malware.example.com",
            "phishing.example.com"
    );

    /**
     * Validate and normalize a URL.
     * Rules (from sample):
     *  - Max 2048 characters
     *  - Must use http or https scheme
     *  - Domain must not be in the blocklist
     *  - Normalize: lowercase, upgrade http→https, strip trailing slash(es)
     *
     * @param url raw URL string
     * @return normalized URL
     * @throws IllegalArgumentException if validation fails
     */
    public static String validate(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }

        if (url.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException(
                    "URL exceeds maximum length of " + MAX_URL_LENGTH + " characters");
        }

        URI parsed;
        try {
            parsed = new URI(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        String scheme = parsed.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("URL must use http or https scheme");
        }

        String host = parsed.getHost();
        if (host == null || BLOCKED_DOMAINS.contains(host.toLowerCase())) {
            throw new IllegalArgumentException("URL domain is blocked");
        }

        // Normalize: lowercase, upgrade http→https, strip trailing slash(es)
        String normalized = url.toLowerCase();
        if (normalized.startsWith("http://")) {
            normalized = "https://" + normalized.substring("http://".length());
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }
}
