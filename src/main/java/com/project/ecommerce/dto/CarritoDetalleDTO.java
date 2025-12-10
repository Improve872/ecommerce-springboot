package com.project.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDetalleDTO {
    private Integer idDetalle;
    private Integer cantidad;
    private BigDecimal subtotal;
    private Integer idCarrito;
    private Integer idProducto;
    private String nombreProducto;
}

