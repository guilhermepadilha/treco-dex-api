package com.treco.dex.api.api.security;

import com.treco.dex.api.domain.model.UserAccount;
import com.treco.dex.api.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserPrincipal(userAccount.getId().toString(), userAccount.getUsername(), userAccount.getPasswordHash());
    }

    public UserPrincipal loadUserById(String userId) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return new UserPrincipal(userAccount.getId().toString(), userAccount.getUsername(), userAccount.getPasswordHash());
    }
}
