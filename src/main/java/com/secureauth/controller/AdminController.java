package com.secureauth.controller;

import com.secureauth.dto.UserDTO;
import com.secureauth.entity.Role;
import com.secureauth.entity.User;
import com.secureauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // LISTAR USUARIOS
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    // FORMULARIO NUEVO USUARIO
    @GetMapping("/users/new")
    public String showNewUserForm(Model model) {
        UserDTO userDto = new UserDTO();
        userDto.setEnabled(true);

        // Asignar rol USER por defecto
        Set<String> defaultRoles = new HashSet<>();
        defaultRoles.add("USER");
        userDto.setRoleNames(defaultRoles);

        model.addAttribute("userDto", userDto);
        model.addAttribute("user", null); // Indica que es nuevo usuario
        return "admin/users/form";
    }

    // CREAR USUARIO
    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("userDto") UserDTO userDto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("user", null);
            return "admin/users/form";
        }

        // Verificar si username ya existe
        if (userService.existsByUsername(userDto.getUsername())) {
            result.rejectValue("username", "error.userDto", "Username ya existe");
            model.addAttribute("user", null);
            return "admin/users/form";
        }

        // Verificar si email ya existe
        if (userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", "error.userDto", "Email ya registrado");
            model.addAttribute("user", null);
            return "admin/users/form";
        }

        // Verificar coincidencia de contraseñas
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.userDto", "Las contraseñas no coinciden");
            model.addAttribute("user", null);
            return "admin/users/form";
        }

        try {
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al crear usuario: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // FORMULARIO EDITAR USUARIO
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(id);
            UserDTO userDto = convertToDto(user);

            model.addAttribute("userDto", userDto);
            model.addAttribute("user", user); // Indica que es edición
            return "admin/users/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Usuario no encontrado: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // ACTUALIZAR USUARIO
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userDto") UserDTO userDto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        // Para edición, no requerir contraseña (usar valores por defecto)
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            userDto.setPassword("TEMP_PASSWORD");
            userDto.setConfirmPassword("TEMP_PASSWORD");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userService.findUserById(id));
            return "admin/users/form";
        }

        try {
            userService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ELIMINAR USUARIO
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            // No permitir eliminar al usuario admin actual (ID 3)
            if (id.equals(3L)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se puede eliminar el usuario administrador principal");
                return "redirect:/admin/users";
            }

            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Convertir User a UserDTO
    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());

        // Convertir roles a lista de nombres
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoleNames(roleNames);

        return dto;
    }
}