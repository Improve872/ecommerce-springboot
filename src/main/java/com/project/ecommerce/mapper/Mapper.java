package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class Mapper {

    private Mapper() {}

    // usuario
    // para mapear la entidad con contraseña a dto sin contraseña
    public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .fechaRegistro(usuario.getFechaRegistro())
                .activo(usuario.getActivo())
                .build();
    }

    // para mapear dto de registro sin contrasena, debe venir de un dto request
    public static Usuario toUsuarioEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        return Usuario.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .rol(dto.getRol())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    // usuario registro
    public static Usuario toUsuarioEntity(RegistroRequestDTO dto) {
        if (dto == null) return null;
        return Usuario.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .build();
    }

    // categoria
    public static CategoriaDTO toCategoriaDTO(Categoria categoria) {
        if (categoria == null) return null;
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .name(categoria.getName())
                .description(categoria.getDescripcion())
                .build();
    }

    public static Categoria toCategoriaEntity(CategoriaDTO dto) {
        if (dto == null) return null;
        return Categoria.builder()
                .id(dto.getId())
                .name(dto.getName())
                .descripcion(dto.getDescription())
                .build();
    }

    // producto
    // para listar y obtener detalles si el fronted necesita
    public static ProductoDTO toProductoDTO(Producto producto) {
        if (producto == null) return null;
        return ProductoDTO.builder()
                .id(producto.getId())
                .name(producto.getName())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .disponible(producto.getDisponible())
                .imageUrl(producto.getImageUrl())
                .categoriaId(producto.getCategoryEntity() != null ? producto.getCategoryEntity().getId() : null)
                .categoryName(producto.getCategoryEntity() != null ? producto.getCategoryEntity().getName() : null)
                .fechaCreacion(producto.getFechaCreacion())
                .build();
    }

    // para operaciones crud requiere entidad  categoria
    public static Producto toProductoEntity(ProductoDTO dto, Categoria categoria) {
        if (dto == null) return null;
        Producto producto = Producto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .disponible(dto.getDisponible())
                .imageUrl(dto.getImageUrl())
                .fechaCreacion(dto.getFechaCreacion())
                .build();

        producto.setCategoryEntity(categoria);
        return producto;
    }

    // carrito
    // mapea lista de detalles  y calcula total
    public static CarritoDTO toCarritoDTO(Carrito carrito) {
        if (carrito == null) return null;

        List<CarritoDetalleDTO> detallesDTO = carrito.getDetalles().stream()
                .map(Mapper::toCarritoDetalleDTO)
                .collect(Collectors.toList());

        // Sumar todos los subtotales para obtener el total general
        BigDecimal totalGeneral = detallesDTO.stream()
                .map(CarritoDetalleDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoDTO.builder()
                .idCarrito(carrito.getIdCarrito())
                .idUsuario(carrito.getUsuario() != null ? carrito.getUsuario().getId() : null)
                .detalles(detallesDTO)
                .totalGeneral(totalGeneral)
                .build();
    }

    public static Carrito toCarritoEntity(CarritoDTO dto, Usuario usuario) {
        if (dto == null) return null;
        return Carrito.builder()
                .idCarrito(dto.getIdCarrito())
                .usuario(usuario)
                .build();
    }

    public static Carrito toCarritoEntity(CarritoDTO dto) {
        if (dto == null) return null;
        return Carrito.builder()
                .idCarrito(dto.getIdCarrito())
                .build();
    }

    // carrito detalle
    public static CarritoDetalleDTO toCarritoDetalleDTO(CarritoDetalle detalle) {
        if (detalle == null) return null;
        return CarritoDetalleDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .idCarrito(detalle.getCarrito() != null ? detalle.getCarrito().getIdCarrito() : null)
                .idProducto(detalle.getProducto() != null ? detalle.getProducto().getId() : null)
                .nombreProducto(detalle.getProducto() != null ? detalle.getProducto().getName() : null)
                .build();
    }

    public static CarritoDetalle toCarritoDetalleEntity(CarritoDetalleDTO dto, Carrito carrito, Producto producto) {
        if (dto == null) return null;
        return CarritoDetalle.builder()
                .idDetalle(dto.getIdDetalle())
                .cantidad(dto.getCantidad())
                .subtotal(dto.getSubtotal())
                .carrito(carrito)
                .producto(producto)
                .build();
    }

    public static CarritoDetalle toCarritoDetalleEntity(CarritoDetalleDTO dto) {
        if (dto == null) return null;
        return CarritoDetalle.builder()
                .idDetalle(dto.getIdDetalle())
                .cantidad(dto.getCantidad())
                .subtotal(dto.getSubtotal())
                .build();
    }

    // pedido
    public static PedidoDTO toPedidoDTO(Pedido pedido) {
        if (pedido == null) return null;

        List<PedidoDetalleDTO> detallesDTO = pedido.getDetalles().stream()
                .map(Mapper::toPedidoDetalleDTO)
                .toList();

        return PedidoDTO.builder()
                .idPedido(pedido.getIdPedido())
                .idUsuario(pedido.getUsuario().getId())
                // 🛑 CORRECCIÓN CLAVE: Usamos el nuevo campo: fechaPedido (en DTO) y getFechaPedido (en Entidad)
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado().name())
                .total(pedido.getTotal())
                .detalles(detallesDTO)
                .build();
    }

    public static PedidoDetalleDTO toPedidoDetalleDTO(PedidoDetalle detalle) {
        return PedidoDetalleDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .idProducto(detalle.getProducto().getId())
                .nombreProducto(detalle.getProducto().getName())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}



