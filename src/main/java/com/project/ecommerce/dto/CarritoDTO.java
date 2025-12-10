package com.project.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;

    private List<CarritoDetalleDTO> detalles;

    private BigDecimal totalGeneral;
}
