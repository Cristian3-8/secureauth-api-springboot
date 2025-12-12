package com.secureauth.controller;

import com.secureauth.dto.RegisterDTO;
import com.secureauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    // FORMULARIO REGISTRO
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDTO());
        }
        return "auth/register";
    }

    // PROCESAR REGISTRO
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterDTO registerDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }

        // Verificar coincidencia de contraseñas
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDto",
                    "Las contraseñas no coinciden");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }

        // Verificar si username ya existe
        if (userService.existsByUsername(registerDto.getUsername())) {
            result.rejectValue("username", "error.registerDto",
                    "El nombre de usuario ya existe");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }

        // Verificar si email ya existe
        if (userService.existsByEmail(registerDto.getEmail())) {
            result.rejectValue("email", "error.registerDto",
                    "El email ya está registrado");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }

        try {
            // Crear usuario con rol USER por defecto
            userService.registerUser(registerDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error en el registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("registerDto", registerDto);
            return "redirect:/auth/register";
        }
    }
}