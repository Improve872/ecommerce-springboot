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
    private BCryptPasswordEncoder passwordEncoder; // inyeccion del codificador de appconfig

    @Override
    public void run(String... args) throws Exception {
        // verificar si existe usuario admin
        if (usuarioRepository.findByCorreoIgnoreCase("admin@ecommerce.com").isEmpty()) {

            // encriptar la  contraseña
            String hashedPassword = passwordEncoder.encode("admin123");

            // crear objeto usuario
            Usuario admin = Usuario.builder()
                    .nombre("Administrador Principal")
                    .correo("admin@ecommerce.com")
                    .contrasena(hashedPassword) // ⬅️ Contraseña hasheada
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            // guardar en la bd
            usuarioRepository.save(admin);
            System.out.println("ADMINISTRADOR INICIAL REGISTRADO: admin@ecommerce.com / admin123");
        }

        // verificar si existe un usuario cliente
        if (usuarioRepository.findByCorreoIgnoreCase("cliente@ecommerce.com").isEmpty()) {

            // encriptar contraseña
            String hashedPassword = passwordEncoder.encode("cliente123");

            // crear objeto usuario
            Usuario cliente = Usuario.builder()
                    .nombre("Cliente de Prueba")
                    .correo("cliente@ecommerce.com")
                    .contrasena(hashedPassword) // ⬅️ Contraseña hasheada
                    .rol(RolUsuario.CLIENTE)
                    .activo(true)
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            // guardar en la bd
            usuarioRepository.save(cliente);
            System.out.println("CLIENTE INICIAL REGISTRADO: cliente@ecommerce.com / cliente123");
        }
    }
}
