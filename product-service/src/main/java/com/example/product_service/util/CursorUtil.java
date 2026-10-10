package com.example.product_service.util;

import com.example.product_service.Exception.InvalidCursorException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.UUID;

public class CursorUtil {

    private static final String DELIMITER = "_";

    public static String encode(Instant createdAt, UUID id) {
        String raw = createdAt.toString() + DELIMITER + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return Cursor.empty();
        }

        String raw;
        try {
            raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidCursorException("Cursor is not valid base64");
        }

        String[] parts = raw.split(DELIMITER, 2);
        if (parts.length != 2) {
            throw new InvalidCursorException("Cursor format is invalid");
        }

        Instant createdAt;
        try {
            createdAt = Instant.parse(parts[0]);
        } catch (DateTimeParseException e) {
            throw new InvalidCursorException("Cursor timestamp is invalid");
        }

        UUID id;
        try {
            id = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new InvalidCursorException("Cursor id is invalid");
        }

        return new Cursor(createdAt, id);
    }

    public record Cursor(Instant createdAt, UUID id) {
        public static Cursor empty() {
            return new Cursor(null, null);
        }
    }
}
