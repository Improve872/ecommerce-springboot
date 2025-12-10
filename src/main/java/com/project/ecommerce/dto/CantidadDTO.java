package com.project.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor // Clave para Jackson
@AllArgsConstructor // Clave para Jackson
public class CantidadDTO {

    private int cantidad;
}