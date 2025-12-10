package com.project.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequestDTO {
    private String nombre;
    private String correo;
    private String contrasena; // ⬅️ Aquí sí necesitamos la contraseña de entrada
    // No necesitas rol ni activo aquí, se asignan en el servicio
}
