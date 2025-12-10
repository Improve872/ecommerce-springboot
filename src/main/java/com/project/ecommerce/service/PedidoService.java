package com.project.ecommerce.service;

import com.project.ecommerce.dto.PedidoDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.*;
import com.project.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final PedidoDetalleRepository pedidoDetalleRepo;
    private final CarritoRepository carritoRepo;

    public PedidoService(
            PedidoRepository pedidoRepo,
            PedidoDetalleRepository pedidoDetalleRepo,
            CarritoRepository carritoRepo
    ) {
        this.pedidoRepo = pedidoRepo;
        this.pedidoDetalleRepo = pedidoDetalleRepo;
        this.carritoRepo = carritoRepo;
    }

    @Transactional
    public PedidoDTO crearPedidoDesdeCarrito(Integer userId) {

        Carrito carrito = carritoRepo.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene carrito."));

        if (carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        Pedido pedido = Pedido.builder()
                .usuario(carrito.getUsuario())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        // Convertimos CarritoDetalle → PedidoDetalle
        for (CarritoDetalle cd : carrito.getDetalles()) {
            PedidoDetalle pd = PedidoDetalle.builder()
                    .pedido(pedido)
                    .producto(cd.getProducto())
                    .cantidad(cd.getCantidad())
                    .subtotal(cd.getSubtotal())
                    .build();

            pedido.getDetalles().add(pd);
            total = total.add(cd.getSubtotal());
        }

        pedido.setTotal(total);
        Pedido saved = pedidoRepo.save(pedido);

        // Vaciar carrito después
        carrito.getDetalles().clear();
        carritoRepo.save(carrito);

        return Mapper.toPedidoDTO(saved);
    }

    public PedidoDTO obtenerPorId(Integer id) {
        return pedidoRepo.findById(id)
                .map(Mapper::toPedidoDTO)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public java.util.List<PedidoDTO> listarPorUsuario(Integer userId) {
        return pedidoRepo.findByUsuarioId(userId).stream()
                .map(Mapper::toPedidoDTO)
                .collect(Collectors.toList());
    }
}

