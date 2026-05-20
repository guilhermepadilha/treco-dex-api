package com.treco.dex.api.domain.repository;

import com.treco.dex.api.BaseIntegrationTest;
import com.treco.dex.api.domain.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserAccountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccount testUser;

    @BeforeEach
    void setUp() {
        testUser = UserAccount.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .build();
    }

    @Test
    void testSaveAndFindUserByUsername() {
        UserAccount saved = userAccountRepository.save(testUser);
        assertNotNull(saved.getId());

        UserAccount found = userAccountRepository.findByUsername("testuser").orElse(null);
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void testFindUserByEmail() {
        userAccountRepository.save(testUser);
        UserAccount found = userAccountRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }
}
