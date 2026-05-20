package com.treco.dex.api.application.service;

import com.treco.dex.api.api.dto.RegisterRequest;
import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount registerUser(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userAccountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        UserAccount saved = userAccountRepository.save(userAccount);
        log.info("User registered successfully: {}", saved.getId());
        return saved;
    }
}
