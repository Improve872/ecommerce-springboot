package com.project.ecommerce.dto;

import com.project.ecommerce.model.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}
