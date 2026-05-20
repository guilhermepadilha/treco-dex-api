package com.treco.dex.api.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserAccountTest {

    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testUserAccountCreation() {
        assertNotNull(userAccount.getId());
        assertEquals("testuser", userAccount.getUsername());
        assertEquals("test@example.com", userAccount.getEmail());
        assertNotNull(userAccount.getCreatedAt());
    }
}
