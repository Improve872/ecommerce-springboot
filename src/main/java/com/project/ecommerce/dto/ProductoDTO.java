package com.project.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    // 1. Campos Identificadores y de Control (Lectura)
    private Integer id;
    private LocalDateTime fechaCreacion; // Fecha de creación (solo lectura)

    // 2. Campos de Producto (Entrada y Salida)
    private String name;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Boolean disponible;
    private String imageUrl; // URL de la imagen

    // 3. Campos de Relación (Importantes para el Frontend/Backend)

    // 🛑 Para la ESCRITURA (POST/PUT): El cliente envía el ID de la Categoría
    private Integer categoriaId;

    // ✅ Para la LECTURA (GET): El servidor devuelve el nombre de la Categoría
    private String categoryName; // Cambié el nombre del campo 'category' a 'categoryName' por claridad
}



