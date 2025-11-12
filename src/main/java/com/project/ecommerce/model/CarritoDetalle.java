package com.project.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_detalle")
public class CarritoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle") // 👈 Coincide con la columna real en la BD
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_carrito", nullable = false) // 👈 nombre real en la BD
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false) // 👈 nombre real en la BD
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double subtotal;

    // 🔹 Constructor vacío (obligatorio para JPA)
    public CarritoDetalle() {
    }

    // 🔹 Constructor con parámetros (opcional, útil para crear objetos)
    public CarritoDetalle(Carrito carrito, Producto producto, Integer cantidad, Double subtotal) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // 🔹 Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
