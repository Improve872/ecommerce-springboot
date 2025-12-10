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
@Data // ⬅️ Reemplaza todos los Getters y Setters manuales
@NoArgsConstructor // ⬅️ Constructor por defecto (requerido por JPA)
@AllArgsConstructor // ⬅️ Constructor con todos los campos
@Builder // ⬅️ Patrón de construcción
public class CarritoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // Relación ManyToOne con el Carrito padre
    @ManyToOne
    @JoinColumn(name = "id_carrito")
    @JsonBackReference
    private Carrito carrito;

    // Relación ManyToOne con el Producto
    @ManyToOne(fetch = FetchType.EAGER) // 🛑 APLICA O VERIFICA ESTO
    @JoinColumn(name = "id_producto")
    @JsonBackReference // O el que uses para evitar ciclos JSON
    private Producto producto;

    private Integer cantidad;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;


}

