package com.treco.dex.api.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserPrincipalTest {

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal("user-id", "testuser", "hashedpassword");
    }

    @Test
    void testUserPrincipalCreation() {
        assertEquals("user-id", userPrincipal.getId());
        assertEquals("testuser", userPrincipal.getUsername());
        assertTrue(userPrincipal.isEnabled());
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testUserPrincipalAuthorities() {
        assertFalse(userPrincipal.getAuthorities().isEmpty());
    }
}
