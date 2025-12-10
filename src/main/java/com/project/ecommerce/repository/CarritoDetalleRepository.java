package com.project.ecommerce.repository;

import com.project.ecommerce.model.Carrito;
import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Integer> {

    Optional<CarritoDetalle> findByCarritoIdCarritoAndProductoId(Integer carritoId, Integer productoId);

    Optional<CarritoDetalle> findByCarritoAndProducto(Carrito carrito, Producto producto);
}
