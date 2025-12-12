package com.secureauth.config;

import com.secureauth.entity.Role;
import com.secureauth.entity.User;
import com.secureauth.repository.RoleRepository;
import com.secureauth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            System.out.println("=== INICIANDO CONFIGURACIÓN DE DATOS ===");

            // 1. CREAR ROLES BÁSICOS
            // Usar findByName() en lugar de existsByName()

            // Rol USER
            Optional<Role> userRoleOpt = roleRepository.findByName("USER");
            if (userRoleOpt.isEmpty()) {
                Role userRole = new Role("USER");
                roleRepository.save(userRole);
                System.out.println("✅ Rol USER creado");
            } else {
                System.out.println("ℹ️  Rol USER ya existe");
            }

            // Rol ADMIN
            Optional<Role> adminRoleOpt = roleRepository.findByName("ADMIN");
            if (adminRoleOpt.isEmpty()) {
                Role adminRole = new Role("ADMIN");
                roleRepository.save(adminRole);
                System.out.println("✅ Rol ADMIN creado");
            } else {
                System.out.println("ℹ️  Rol ADMIN ya existe");
            }

            // Rol MODERATOR (opcional)
            Optional<Role> moderatorRoleOpt = roleRepository.findByName("MODERATOR");
            if (moderatorRoleOpt.isEmpty()) {
                Role moderatorRole = new Role("MODERATOR");
                roleRepository.save(moderatorRole);
                System.out.println("✅ Rol MODERATOR creado");
            } else {
                System.out.println("ℹ️  Rol MODERATOR ya existe");
            }

            // 2. CREAR USUARIO ADMINISTRADOR
            try {
                if (userRepository.findByUsername("admin").isEmpty()) {
                    User admin = new User("admin", "admin@secureauth.com", passwordEncoder.encode("admin123"));
                    admin.setFirstName("Administrador");
                    admin.setLastName("Sistema");
                    admin.setEnabled(true);

                    // Buscar rol ADMIN
                    Role adminRole = roleRepository.findByName("ADMIN")
                            .orElseGet(() -> {
                                Role newRole = new Role("ADMIN");
                                return roleRepository.save(newRole);
                            });

                    admin.getRoles().add(adminRole);
                    userRepository.save(admin);

                    System.out.println("✅ Usuario ADMIN creado:");
                    System.out.println("   Usuario: admin");
                    System.out.println("   Contraseña: admin123");
                } else {
                    System.out.println("ℹ️  Usuario ADMIN ya existe");
                }
            } catch (Exception e) {
                System.out.println("⚠️  Error creando usuario admin: " + e.getMessage());
            }

            // 3. CREAR USUARIO REGULAR (opcional)
            try {
                if (userRepository.findByUsername("usuario").isEmpty()) {
                    User user = new User("usuario", "usuario@secureauth.com", passwordEncoder.encode("password123"));
                    user.setFirstName("Usuario");
                    user.setLastName("Prueba");
                    user.setEnabled(true);

                    // Buscar rol USER
                    Role userRole = roleRepository.findByName("USER")
                            .orElseGet(() -> {
                                Role newRole = new Role("USER");
                                return roleRepository.save(newRole);
                            });

                    user.getRoles().add(userRole);
                    userRepository.save(user);

                    System.out.println("✅ Usuario REGULAR creado:");
                    System.out.println("   Usuario: usuario");
                    System.out.println("   Contraseña: password123");
                } else {
                    System.out.println("ℹ️  Usuario REGULAR ya existe");
                }
            } catch (Exception e) {
                System.out.println("⚠️  Error creando usuario regular: " + e.getMessage());
            }

            System.out.println("=== CONFIGURACIÓN DE DATOS COMPLETADA ===");
        };
    }
}