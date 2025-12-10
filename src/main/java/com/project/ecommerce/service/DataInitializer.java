package com.project.ecommerce.service;

import com.project.ecommerce.model.RolUsuario;
import com.project.ecommerce.model.Usuario;
import com.project.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Inyectamos el codificador de AppConfig

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe un usuario admin
        if (usuarioRepository.findByCorreoIgnoreCase("admin@ecommerce.com").isEmpty()) {

            // 1. Encriptar la contraseña
            String hashedPassword = passwordEncoder.encode("admin123");

            // 2. Crear el objeto Usuario
            Usuario admin = Usuario.builder()
                    .nombre("Administrador Principal")
                    .correo("admin@ecommerce.com")
                    .contrasena(hashedPassword) // ⬅️ Contraseña hasheada
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            // 3. Guardar en la base de datos
            usuarioRepository.save(admin);
            System.out.println("ADMINISTRADOR INICIAL REGISTRADO: admin@ecommerce.com / admin123");
        }

        // Verificar si ya existe un usuario cliente
        if (usuarioRepository.findByCorreoIgnoreCase("cliente@ecommerce.com").isEmpty()) {

            // 1. Encriptar la contraseña
            String hashedPassword = passwordEncoder.encode("cliente123");

            // 2. Crear el objeto Usuario
            Usuario cliente = Usuario.builder()
                    .nombre("Cliente de Prueba")
                    .correo("cliente@ecommerce.com")
                    .contrasena(hashedPassword) // ⬅️ Contraseña hasheada
                    .rol(RolUsuario.CLIENTE)
                    .activo(true)
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            // 3. Guardar en la base de datos
            usuarioRepository.save(cliente);
            System.out.println("CLIENTE INICIAL REGISTRADO: cliente@ecommerce.com / cliente123");
        }
    }
}
