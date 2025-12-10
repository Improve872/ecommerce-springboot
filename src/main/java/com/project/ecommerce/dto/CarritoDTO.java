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

    // ✅ NUEVO: Lista de ítems en el carrito (mapeados desde CarritoDetalle)
    private List<CarritoDetalleDTO> detalles;

    // ✅ NUEVO: Total calculado por el mapper
    private BigDecimal totalGeneral;
}
