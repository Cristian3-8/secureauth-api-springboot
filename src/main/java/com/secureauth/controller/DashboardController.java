package com.secureauth.controller;

import com.secureauth.entity.User;
import com.secureauth.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;

    // Inyectar UserService para estadísticas
    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard() {
        // Redirigido por SecurityConfig
        return "redirect:/dashboard/role";
    }

    @GetMapping("/dashboard/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard Administrador");

        // Estadísticas DINÁMICAS
        long totalUsers = userService.countAllUsers();
        long activeUsers = userService.countEnabledUsers();
        int totalRoles = 2; // ADMIN y USER (puedes hacerlo dinámico si quieres)

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalRoles", totalRoles);

        return "admin/dashboard";
    }

    @GetMapping("/dashboard/user")
    @PreAuthorize("hasRole('USER')")
    public String userDashboard(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Dashboard Usuario");

        // IMPORTANTE: Pasar el usuario al modelo
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        return "user/dashboard";
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')")
    public String userProfile(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Mi Perfil");

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        return "user/profile";
    }

    @GetMapping("/dashboard/role")
    @PreAuthorize("isAuthenticated()")
    public String dashboardByRole(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

            if (isAdmin) {
                return "redirect:/dashboard/admin";
            } else if (isUser) {
                return "redirect:/dashboard/user";
            }
        }

        return "redirect:/";
    }
}