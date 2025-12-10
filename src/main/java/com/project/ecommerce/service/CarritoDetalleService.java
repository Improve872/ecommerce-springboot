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

    // agrega o actualiza un producto en el carrito
    public CarritoDetalleDTO agregarOActualizarItem(CarritoDetalleDTO dto) {
        //  obtener carrito y producto
        Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado."));
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

        // buscar si el producto existe en el carrito
        Optional<CarritoDetalle> existingDetalle = carritoDetalleRepository
                .findByCarritoIdCarritoAndProductoId(carrito.getIdCarrito(), producto.getId());

        CarritoDetalle detalle;
        Integer cantidadAAgregar = dto.getCantidad() != null ? dto.getCantidad() : 1;

        if (existingDetalle.isPresent()) {
            // sumar la cantidad
            detalle = existingDetalle.get();
            detalle.setCantidad(detalle.getCantidad() + cantidadAAgregar);
        } else {
            // crear nuevo detalle
            detalle = CarritoDetalle.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidadAAgregar)
                    .build();
        }

        // calcular subtotal
        detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(detalle.getCantidad())));

        // guardar
        CarritoDetalle savedDetalle = carritoDetalleRepository.save(detalle);
        return Mapper.toCarritoDetalleDTO(savedDetalle);
    }

    // metodo para cambiar la cantidad directamente
    public CarritoDetalleDTO modificarCantidad(Integer idDetalle, Integer nuevaCantidad) {
        return carritoDetalleRepository.findById(idDetalle)
                .map(detalle -> {
                    if (nuevaCantidad <= 0) {
                        carritoDetalleRepository.delete(detalle);
                        return null; // Ítem eliminado
                    }

                    Producto producto = detalle.getProducto();

                    detalle.setCantidad(nuevaCantidad);

                    // recalcular subtotal
                    detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(nuevaCantidad)));

                    CarritoDetalle updated = carritoDetalleRepository.save(detalle);
                    return Mapper.toCarritoDetalleDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Detalle de Carrito no encontrado."));
    }

    // metodos crud restantes

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

