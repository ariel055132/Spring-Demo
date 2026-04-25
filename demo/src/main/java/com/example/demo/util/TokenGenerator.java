package com.example.demo.util;

import com.example.demo.repository.QRCodeRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 + Base62 token generator with collision retry.
 * Matches the logic from the qr_code_generator reference implementation.
 */
public class TokenGenerator {

    private static final String BASE62_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 7;
    private static final int MAX_RETRIES = 10;

    /**
     * Generate a unique 7-char token derived from the URL via SHA-256 + Base62.
     * A nonce (attempt index) is mixed in on collision to produce a different token.
     *
     * @param url        normalized URL to derive the token from
     * @param repository used to check token uniqueness
     * @return unique 7-char token
     */
    public static String generate(String url, QRCodeRepository repository) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String input = url + attempt;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                String token = base62Encode(hash).substring(0, TOKEN_LENGTH);
                if (!repository.existsByShortCode(token)) {
                    return token;
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 not available", e);
            }
        }
        throw new RuntimeException("Token generation failed: all retries exhausted");
    }

    private static String base62Encode(byte[] data) {
        BigInteger num = new BigInteger(1, data);
        if (num.equals(BigInteger.ZERO)) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }
        StringBuilder result = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = num.divideAndRemainder(base);
            result.append(BASE62_CHARS.charAt(divRem[1].intValue()));
            num = divRem[0];
        }
        return result.reverse().toString();
    }
}
