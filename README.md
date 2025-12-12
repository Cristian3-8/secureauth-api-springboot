🔐 SecureAuth API - Sistema de Autenticación Spring Boot
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

Sistema completo de autenticación y gestión de usuarios implementado con Spring Boot 6 y Spring Security 6.

✨ Características: 
✅ Autenticación segura con Spring Security 6

✅ Dashboard dual (Administrador/Usuario normal)

✅ CRUD completo de usuarios

✅ Sistema de roles (ADMIN/USER) con permisos diferenciados

✅ Interfaz moderna con Bootstrap 5 responsive

✅ Validaciones backend y frontend

✅ Seguridad robusta: BCrypt, CSRF, protección de rutas

🛠️ Tecnologías
Backend: Java 17, Spring Boot 3.5.8, Spring Security 6, Spring Data JPA

Frontend: Thymeleaf 3, Bootstrap 5.1.3, Bootstrap Icons, JavaScript

Base de datos: MySQL/MariaDB

Herramientas: Maven, Spring Boot DevTools


🚀 Instalación
Prerrequisitos
Java 17 o superior
Maven 3.9+
MySQL 8.0+

Pasos
Clonar repositorio:

bash
git clone https://github.com/tu-usuario/secureauth-api-springboot.git
cd secureauth-api-springboot
Crear base de datos MySQL:

sql
CREATE DATABASE secureauth_db;
Configurar application.properties:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/secureauth_db
spring.datasource.username=root
spring.datasource.password=tu_password
Ejecutar aplicación:

bash
mvn spring-boot:run
Acceder a: http://localhost:8081

Credenciales por defecto
Administrador: admin / admin123

Usuario: usuario / password123

📁 Estructura del Proyecto

src/main/java/com/secureauth/

├── config/           # Configuraciones (Security, DataSeeder)
|
├── controller/       # Controladores (Admin, Dashboard, Home)
|
├── dto/             # Data Transfer Objects
|
├── entity/          # Entidades JPA (User, Role)
|
├── repository/      # Repositorios Spring Data JPA
|
├── service/         # Lógica de negocio
|
└── util/            # Utilidades
