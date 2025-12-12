package com.secureauth.repository;

import com.secureauth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // ESTE METODO ES EL QUE FALTA:
    Optional<Role> findByName(String name);
}