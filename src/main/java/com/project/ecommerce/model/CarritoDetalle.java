package com.project.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data; // ⚠️ NUEVO: Genera Getters, Setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // ⚠️ NUEVO: Constructor sin argumentos
import lombok.AllArgsConstructor; // ⚠️ NUEVO: Constructor con todos los argumentos
import lombok.Builder; // ⚠️ NUEVO: Patrón Builder

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

