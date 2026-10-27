package com.wisteria.model;

public record User(
        int userId,
        String fullName,
        String email,
        String passwordHash,
        String role,
        String status,
        String createdAt
) {
}