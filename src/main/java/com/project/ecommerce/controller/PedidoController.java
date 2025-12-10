package com.project.ecommerce.controller;

import com.project.ecommerce.dto.PedidoDTO;
import com.project.ecommerce.model.Usuario;
import com.project.ecommerce.service.PedidoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    @PostMapping("/api/v1/pedidos/crear/{userId}")
    public PedidoDTO crearPedido(@PathVariable Integer userId) {


        return pedidoService.crearPedidoDesdeCarrito(userId);
    }


    @GetMapping("/api/v1/pedidos/mis-pedidos")
    public List<PedidoDTO> misPedidos(@AuthenticationPrincipal Usuario usuario) {


        // verificando que el usuario no sea nulo antes de llamar a getid
        if (usuario == null) {
            // si el filtro falla
            throw new RuntimeException("Usuario no autenticado para listar pedidos.");
        }
        return pedidoService.listarPorUsuario(usuario.getId());
    }
}


