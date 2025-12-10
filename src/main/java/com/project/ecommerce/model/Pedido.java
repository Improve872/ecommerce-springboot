package com.project.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_pedido")
    @Builder.Default
    private LocalDateTime fechaPedido = LocalDateTime.now(); // Renombrado de fechaCreacion a fechaPedido para consistencia

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<PedidoDetalle> detalles = new ArrayList<>();
}
