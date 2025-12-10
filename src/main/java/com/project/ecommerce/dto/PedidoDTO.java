package com.project.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Integer idPedido;
    private Integer idUsuario;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private List<PedidoDetalleDTO> detalles;
}
