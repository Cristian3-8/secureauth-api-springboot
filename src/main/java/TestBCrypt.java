package com.secureauth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        System.out.println("=== GENERANDO HASH PARA admin123 ===");
        String hashAdmin = encoder.encode("admin123");
        System.out.println("Hash: " + hashAdmin);
        System.out.println("Longitud: " + hashAdmin.length());
        System.out.println("¿Es BCrypt válido? " + hashAdmin.matches("\\$2[aby]\\$\\d{2}\\$.{53}"));

        System.out.println("\n=== VERIFICANDO HASH ACTUAL DE admin ===");
        String hashActualAdmin = "$2a$12$V7pWnBq.R8oC6sT3uV2wX4yZ5A6B7C8D9E0F1G2H3I4J5K6L7M8N9O0P1Q2R3S4";
        System.out.println("Hash actual: " + hashActualAdmin);
        System.out.println("Longitud: " + hashActualAdmin.length());
        System.out.println("¿Es BCrypt válido? " + hashActualAdmin.matches("\\$2[aby]\\$\\d{2}\\$.{53}"));
        System.out.println("¿admin123 coincide con hash actual? " + encoder.matches("admin123", hashActualAdmin));

        System.out.println("\n=== VERIFICANDO HASH DE usuario ===");
        String hashUsuario = "$2a$12$Q.rnwOOvnw4RQEJk1ah90uGbrKlsibvArEjp46He7bb8oo09PUY5S";
        System.out.println("Hash usuario: " + hashUsuario);
        System.out.println("Longitud: " + hashUsuario.length());
        System.out.println("¿Es BCrypt válido? " + hashUsuario.matches("\\$2[aby]\\$\\d{2}\\$.{53}"));
        System.out.println("¿password123 coincide? " + encoder.matches("password123", hashUsuario));
    }
}