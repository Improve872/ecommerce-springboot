package com.project.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "carrito_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    @JsonBackReference
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    @JsonBackReference
    private Producto producto;

    private Integer cantidad;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;


}

