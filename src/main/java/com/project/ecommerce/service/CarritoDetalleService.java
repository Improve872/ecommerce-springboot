package com.project.ecommerce.service;

import com.project.ecommerce.dto.CarritoDetalleDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.Carrito;
import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.model.Producto;
import com.project.ecommerce.repository.CarritoDetalleRepository;
import com.project.ecommerce.repository.CarritoRepository;
import com.project.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // ⚠️ MÉTODO CRÍTICO: Agrega o actualiza un producto en el carrito (une lógica de guardar y actualizar)
    public CarritoDetalleDTO agregarOActualizarItem(CarritoDetalleDTO dto) {
        // 1. Obtener Carrito y Producto
        Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado."));
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

        // 2. Buscar si ya existe este producto en este carrito (usa el método que añadiste)
        Optional<CarritoDetalle> existingDetalle = carritoDetalleRepository
                .findByCarritoIdCarritoAndProductoId(carrito.getIdCarrito(), producto.getId());

        CarritoDetalle detalle;
        Integer cantidadAAgregar = dto.getCantidad() != null ? dto.getCantidad() : 1;

        if (existingDetalle.isPresent()) {
            // 3a. ACTUALIZAR: Sumar la cantidad
            detalle = existingDetalle.get();
            detalle.setCantidad(detalle.getCantidad() + cantidadAAgregar);
        } else {
            // 3b. CREAR: Nuevo detalle
            detalle = CarritoDetalle.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidadAAgregar)
                    .build();
        }

        // 4. CALCULAR SUBTOTAL: El subtotal es siempre calculado por el backend
        detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(detalle.getCantidad())));

        // 5. Guardar
        CarritoDetalle savedDetalle = carritoDetalleRepository.save(detalle);
        return Mapper.toCarritoDetalleDTO(savedDetalle);
    }

    // ⚠️ Modificación del método actualizar para cambiar la cantidad directamente
    public CarritoDetalleDTO modificarCantidad(Integer idDetalle, Integer nuevaCantidad) {
        return carritoDetalleRepository.findById(idDetalle)
                .map(detalle -> {
                    if (nuevaCantidad <= 0) {
                        carritoDetalleRepository.delete(detalle);
                        return null; // Ítem eliminado
                    }

                    // Asegurar que el precio del producto se use para el cálculo
                    Producto producto = detalle.getProducto();

                    detalle.setCantidad(nuevaCantidad);

                    // Recalcular subtotal
                    detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(nuevaCantidad)));

                    CarritoDetalle updated = carritoDetalleRepository.save(detalle);
                    return Mapper.toCarritoDetalleDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Detalle de Carrito no encontrado."));
    }

    // Métodos CRUD básicos restantes...

    public List<CarritoDetalleDTO> listarTodos() {
        return carritoDetalleRepository.findAll().stream()
                .map(Mapper::toCarritoDetalleDTO)
                .collect(Collectors.toList());
    }

    public CarritoDetalleDTO obtenerPorId(Integer id) {
        return carritoDetalleRepository.findById(id)
                .map(Mapper::toCarritoDetalleDTO)
                .orElseThrow(() -> new RuntimeException("Detalle de Carrito no encontrado con ID: " + id));
    }

    public void eliminar(Integer id) {
        carritoDetalleRepository.deleteById(id);
    }
}

