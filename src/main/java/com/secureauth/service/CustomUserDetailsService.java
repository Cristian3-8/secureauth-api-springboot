package com.secureauth.service;

import com.secureauth.entity.User;
import com.secureauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        System.out.println("=== DEBUG: Cargando usuario ===");
        System.out.println("Usuario: " + user.getUsername());
        System.out.println("Contraseña (hash): " + user.getPassword());
        System.out.println("Roles: " + user.getAuthorities());
        System.out.println("Habilitado: " + user.isEnabled());

        return user;
    }
}