package com.project.ecommerce.controller;

import com.project.ecommerce.dto.PedidoDTO;
import com.project.ecommerce.model.Usuario;
import com.project.ecommerce.service.PedidoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 🛑 CAMBIO 1: ELIMINAR/COMENTAR el RequestMapping a nivel de clase
// @RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Crear pedido usando carrito
    // 🛑 CAMBIO 2: USAR LA RUTA COMPLETA Y CAPTURAR EL ID DEL PATH
    // Esto coincide con el fetch del frontend: /api/v1/pedidos/crear/9
    @PostMapping("/api/v1/pedidos/crear/{userId}")
    public PedidoDTO crearPedido(@PathVariable Integer userId) {

        // 🛑 CAMBIO 3: ELIMINAR @AuthenticationPrincipal
        // Ya que el endpoint es ahora público (permitAll), obtenemos el ID del PathVariable.
        return pedidoService.crearPedidoDesdeCarrito(userId);
    }

    // Listar pedidos del usuario autenticado
    // 🛑 CAMBIO 4: AJUSTAR EL GET PARA USAR LA RUTA COMPLETA (PROTEGIDA)
    @GetMapping("/api/v1/pedidos/mis-pedidos")
    public List<PedidoDTO> misPedidos(@AuthenticationPrincipal Usuario usuario) {
        // NOTA: Este método seguirá requiriendo un token válido (401 si no hay token)
        // porque no lo hicimos 'permitAll()' en SecurityConfig.

        // Aquí debes verificar que 'usuario' no sea nulo antes de llamar a getId()
        if (usuario == null) {
            // Esto solo ocurre si el filtro JWT falla
            throw new RuntimeException("Usuario no autenticado para listar pedidos.");
        }
        return pedidoService.listarPorUsuario(usuario.getId());
    }
}


