package com.project.ecommerce.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.Collection; // Importar Collection
import java.util.Collections; // Importar Collections
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// Imports de Spring Security
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    private String nombre;
    private String correo;

    // Se asume que la corrección de la BD a 'contrasena' (sin tilde) funcionó
    // o que estás usando el nombre que fuerza tu IDE. Usaremos el nombre que mejor coincide con la BD.
    @Column(name = "contrasena", nullable = false, length = 60)
    private String contrasena; // La contraseña hasheada

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    private Boolean activo;

    // Relación One-to-Many con Carrito
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Carrito> carritos;

    // --- Lógica del Ciclo de Vida JPA (PrePersist) ---
    @PrePersist
    protected void onCreate() {
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    // =======================================================
    // 🛡️ IMPLEMENTACIÓN DE USERDETAILS PARA SPRING SECURITY
    // =======================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte el RolUsuario (ej. CLIENTE o ADMIN) en una autoridad de Spring Security (ej. ROLE_CLIENTE)
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + this.rol.name())
        );
    }

    @Override
    public String getPassword() {
        // Devuelve la contraseña hasheada almacenada en la BD
        return this.contrasena;
    }

    @Override
    public String getUsername() {
        // Spring Security usará el correo como nombre de usuario para el login
        return this.correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Dejar como true si no implementas lógica de expiración de cuenta
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Dejar como true si no implementas lógica de bloqueo de cuenta
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Dejar como true si no implementas lógica de expiración de credenciales
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 🛑 CORRECCIÓN: Si 'activo' es null, tratamos la cuenta como deshabilitada (false).
        return this.activo != null && this.activo;
    }
}

