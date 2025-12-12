package com.secureauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Inicio - SecureAuth");
        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return home(model);
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Iniciar Sesión - SecureAuth");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerRedirect() {
        return "redirect:/auth/register";
    }
}