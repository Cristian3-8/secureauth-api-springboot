package com.secureauth.service;

import com.secureauth.dto.UserDTO;
import com.secureauth.dto.RegisterDTO;
import com.secureauth.entity.Role;
import com.secureauth.entity.User;
import com.secureauth.exception.UserNotFoundException;
import com.secureauth.repository.RoleRepository;
import com.secureauth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODOS NUEVOS para DashboardController:

    public long countAllUsers() {
        return userRepository.count();
    }

    public long countEnabledUsers() {
        return userRepository.countByEnabled(true);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }

    // ========== MÉTODOS CRUD ==========

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    public User createUser(UserDTO userDto) {
        // Validaciones
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(userDto.isEnabled());
        user.setCreatedAt(LocalDateTime.now());

        // Asignar roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : userDto.getRoleNames()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role newRole = new Role(roleName);
                        return roleRepository.save(newRole);
                    });
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User registerUser(RegisterDTO registerDto) {
        // Validaciones
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    return roleRepository.save(role);
                });
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO userDto) {
        User user = findUserById(id);

        // Actualizar email si es diferente
        if (!user.getEmail().equals(userDto.getEmail())) {
            // Verificar que el nuevo email no exista
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado por otro usuario");
            }
            user.setEmail(userDto.getEmail());
        }

        user.setEnabled(userDto.isEnabled());

        // Actualizar roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : userDto.getRoleNames()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role newRole = new Role(roleName);
                        return roleRepository.save(newRole);
                    });
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);

        // Proteger usuario admin (ID 3)
        if (id.equals(3L)) {
            throw new RuntimeException("No se puede eliminar el usuario administrador principal");
        }

        userRepository.delete(user);
    }

    // ========== MÉTODOS UTILITARIOS ==========

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            }
        }
        throw new RuntimeException("No hay usuario autenticado");
    }
}