package com.secureauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, message = "Username debe tener al menos 3 caracteres")
    private String username;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @Size(min = 6, message = "Contraseña debe tener al menos 6 caracteres")
    private String password;

    private String confirmPassword;

    private boolean enabled = true;

    @Size(min = 1, message = "Debe asignar al menos un rol")
    private Set<String> roleNames = new HashSet<>();
}