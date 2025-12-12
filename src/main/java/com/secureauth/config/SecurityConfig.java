package com.secureauth.config;

import com.secureauth.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de autorización
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/register",
                                "/auth/register",      // <-- AÑADIR esta ruta
                                "/auth/forgot-password", // <-- AÑADIR esta ruta
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/error",
                                "/favicon.ico"
                        ).permitAll()
                        // Rutas específicas de dashboard
                        .requestMatchers("/dashboard/admin").hasRole("ADMIN")
                        .requestMatchers("/dashboard/user").hasRole("USER")
                        // Rutas que requieren autenticación
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        // Dashboard principal requiere autenticación
                        .requestMatchers("/dashboard").authenticated()
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )

                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .successHandler(authenticationSuccessHandler())  // <-- AÑADIR
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Configuración de logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Configuración de excepciones
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                )

                // Configuración de sesiones
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                )

                // Deshabilitar CSRF solo para desarrollo
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // Handler para redirigir según rol después del login
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Obtener autoridades (roles) del usuario autenticado
            var authorities = authentication.getAuthorities();

            // Verificar si tiene rol ADMIN
            boolean isAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // Verificar si tiene rol USER
            boolean isUser = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

            // Redirigir según el rol
            if (isAdmin) {
                response.sendRedirect("/dashboard/admin");
            } else if (isUser) {
                response.sendRedirect("/dashboard/user");
            } else {
                // Si no tiene rol reconocido, ir al dashboard genérico
                response.sendRedirect("/dashboard");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}