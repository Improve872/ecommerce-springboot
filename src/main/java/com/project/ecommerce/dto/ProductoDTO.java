package com.project.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    private Integer id;
    private LocalDateTime fechaCreacion;
    private String name;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Boolean disponible;
    private String imageUrl;
    private Integer categoriaId;
    private String categoryName;
}



