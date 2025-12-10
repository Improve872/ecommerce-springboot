package com.project.ecommerce.service;

import com.project.ecommerce.dto.CarritoDTO;
import com.project.ecommerce.dto.CarritoDetalleDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.Carrito;
import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.model.Producto;
import com.project.ecommerce.model.Usuario;
import com.project.ecommerce.repository.CarritoRepository;
import com.project.ecommerce.repository.CarritoDetalleRepository;
import com.project.ecommerce.repository.ProductoRepository;
import com.project.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    // metodos de busqueda y creacion del carrito

    @Transactional
    public CarritoDTO getOrCreateCarritoByUser(Integer usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .map(Mapper::toCarritoDTO)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

                    Carrito nuevoCarrito = Carrito.builder().usuario(usuario).build();
                    Carrito savedCarrito = carritoRepository.save(nuevoCarrito);
                    return Mapper.toCarritoDTO(savedCarrito);
                });
    }

    private Carrito getOrCreateCarritoEntityByUser(Integer usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

                    Carrito nuevoCarrito = Carrito.builder().usuario(usuario).build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    // logica

    @Transactional
    public CarritoDTO agregarProducto(Integer userId, CarritoDetalleDTO detalleDto) {

        Carrito carrito = getOrCreateCarritoEntityByUser(userId);

        Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDto.getIdProducto()));

        if (producto.getPrecio() == null) {
            throw new RuntimeException("El producto ID " + producto.getId() + " no tiene precio configurado.");
        }


        Optional<CarritoDetalle> existingDetail = carritoDetalleRepository.findByCarritoAndProducto(carrito, producto);

        CarritoDetalle detalle;
        BigDecimal precioUnitario = producto.getPrecio();
        BigDecimal nuevoSubtotal;

        if (existingDetail.isPresent()) {
            detalle = existingDetail.get();
            int nuevaCantidad = detalle.getCantidad() + detalleDto.getCantidad();
            detalle.setCantidad(nuevaCantidad);

            nuevoSubtotal = precioUnitario.multiply(BigDecimal.valueOf(nuevaCantidad));
            detalle.setSubtotal(nuevoSubtotal);

        } else {
            detalle = CarritoDetalle.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(detalleDto.getCantidad())
                    .build();

            nuevoSubtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            detalle.setSubtotal(nuevoSubtotal);
        }

        carritoDetalleRepository.save(detalle);

        Carrito carritoActualizado = carritoRepository.findById(carrito.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Error interno al obtener carrito."));

        return Mapper.toCarritoDTO(carritoActualizado);
    }

    // metodos de manipulacion


     // actualiza la cantidad de un producto específico en el carrito

    @Transactional
    public CarritoDTO actualizarCantidad(Integer detalleId, int nuevaCantidad, Integer userId) {
        CarritoDetalle detalle = carritoDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle de Carrito no encontrado con ID: " + detalleId));

        // verificación
        if (!detalle.getCarrito().getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acceso denegado: El detalle no pertenece al usuario.");
        }

        if (nuevaCantidad <= 0) {
            // si la cantidad es 0 o menos se elimina
            carritoDetalleRepository.delete(detalle);
        } else {
            // actualizar cantidad y recalcular subtotal
            detalle.setCantidad(nuevaCantidad);

            if (detalle.getProducto().getPrecio() == null) {
                throw new RuntimeException("El producto no tiene precio configurado.");
            }
            BigDecimal precioUnitario = detalle.getProducto().getPrecio();
            BigDecimal nuevoSubtotal = precioUnitario.multiply(BigDecimal.valueOf(nuevaCantidad));
            detalle.setSubtotal(nuevoSubtotal);

            carritoDetalleRepository.save(detalle);
        }

        // recargar y devolver el carrito actualizado
        Carrito carritoActualizado = carritoRepository.findById(detalle.getCarrito().getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Error interno al obtener carrito."));

        return Mapper.toCarritoDTO(carritoActualizado);
    }



     // elimina una línea de producto del carrito por el id del detalle

    @Transactional
    public CarritoDTO eliminarDetalle(Integer detalleId, Integer userId) {
        CarritoDetalle detalle = carritoDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle de Carrito no encontrado con ID: " + detalleId));

        if (!detalle.getCarrito().getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acceso denegado: El detalle no pertenece al usuario.");
        }

        Carrito carrito = detalle.getCarrito();

        // Eliminar detalle
        carritoDetalleRepository.delete(detalle);

        Carrito carritoActualizado = carritoRepository.findById(carrito.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Error interno al obtener carrito."));

        return Mapper.toCarritoDTO(carritoActualizado);
    }


    // vacia el carrito de un usuario

    @Transactional
    public CarritoDTO vaciarCarrito(Integer userId) {
        Carrito carrito = getOrCreateCarritoEntityByUser(userId);

        // eliminar los detalles
        carritoDetalleRepository.deleteAll(carrito.getDetalles());

        carrito.getDetalles().clear();

        // devolver carrito vacio
        Carrito carritoActualizado = carritoRepository.findById(carrito.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Error interno al obtener carrito."));

        return Mapper.toCarritoDTO(carritoActualizado);
    }


    // metodos crud admin

    public List<CarritoDTO> listarTodos() {
        return carritoRepository.findAll().stream()
                .map(Mapper::toCarritoDTO)
                .collect(Collectors.toList());
    }

    public CarritoDTO obtenerPorId(Integer id) {
        return carritoRepository.findById(id)
                .map(Mapper::toCarritoDTO)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + id));
    }

    @Transactional
    public CarritoDTO guardar(CarritoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        Carrito carrito = Mapper.toCarritoEntity(dto, usuario);
        Carrito savedCarrito = carritoRepository.save(carrito);

        return Mapper.toCarritoDTO(savedCarrito);
    }

    @Transactional
    public CarritoDTO actualizar(Integer id, CarritoDTO dto) {
        return carritoRepository.findById(id)
                .map(carrito -> {
                    if (dto.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
                        carrito.setUsuario(usuario);
                    }
                    // Implementar lógica de actualización de estado si es necesario

                    Carrito updated = carritoRepository.save(carrito);
                    return Mapper.toCarritoDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!carritoRepository.existsById(id)) {
            throw new RuntimeException("Carrito no encontrado con ID: " + id);
        }
        carritoRepository.deleteById(id);
    }
}
