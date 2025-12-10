package com.project.ecommerce.repository;

import com.project.ecommerce.model.Carrito;
import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Integer> {

    // 1. Método original (buscando por IDs)
    Optional<CarritoDetalle> findByCarritoIdCarritoAndProductoId(Integer carritoId, Integer productoId);

    // 🛑 2. MÉTODO CRUCIAL PARA EL CARRETOSERVICE (Buscando por Entidades)
    // Spring Data JPA puede inferir el ID de la Entidad automáticamente.
    Optional<CarritoDetalle> findByCarritoAndProducto(Carrito carrito, Producto producto);
}
