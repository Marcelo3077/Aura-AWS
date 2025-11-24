package com.example.aura.Security.Service;

import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.example.aura.Entity.Superuser.Repository.SuperuserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SuperuserRepository<Superuser> superuserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return superuserRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
