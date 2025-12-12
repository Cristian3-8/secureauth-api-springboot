package com.secureauth.repository;

import com.secureauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Estos 3 métodos ya deberías tenerlos:
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);  // <- FALTABA ESTE

    // Agrega estos métodos que faltan:
    Boolean existsByEmail(String email);       // <- FALTABA ESTE

    // Métodos adicionales útiles:
    long countByEnabled(boolean enabled);
    Optional<User> findByIdAndEnabled(Long id, boolean enabled);

    // Para búsquedas (opcional pero útil)
    java.util.List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    java.util.List<User> findByEmailContainingIgnoreCase(String email);
}