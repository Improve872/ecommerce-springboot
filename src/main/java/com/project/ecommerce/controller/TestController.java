package com.project.ecommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Solo usuarios con el rol ADMIN pueden acceder
    public String adminAccess() {
        return "Contenido restringido solo para Administradores.";
    }

    @GetMapping("/publico")
    public String publicAccess() {
        return "Contenido público para todos.";
    }
}