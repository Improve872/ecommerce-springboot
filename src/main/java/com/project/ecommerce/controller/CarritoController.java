package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CarritoDTO;
import com.project.ecommerce.dto.CarritoDetalleDTO;
import com.project.ecommerce.dto.CantidadDTO;
import com.project.ecommerce.security.SecurityUtils;
import com.project.ecommerce.service.CarritoService;
import com.project.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecurityUtils securityUtils;

    // --- MÉTODOS DEL USUARIO (Requiere token) ---

    // 🛑 1. OBTENER CARRITO ACTUAL DEL USUARIO LOGUEADO
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> getCarritoActual() {
        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            CarritoDTO carrito = carritoService.getOrCreateCarritoByUser(userId);

            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🛑 2. AÑADIR/ACTUALIZAR PRODUCTO AL CARRITO
    @PostMapping("/agregar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> agregarProductoAlCarrito(@RequestBody CarritoDetalleDTO detalleDto) {
        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            CarritoDTO carritoActualizado = carritoService.agregarProducto(userId, detalleDto);

            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            // Captura Producto no encontrado o precio NULL
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        }
    }

    // 🛑 3. ACTUALIZAR CANTIDAD DE UN DETALLE (USANDO EL NUEVO DTO)
    @PutMapping("/actualizar/{detalleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Integer detalleId,
            @RequestBody CantidadDTO cantidadDto) { // Usando el DTO importado

        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            // 🛑 CORRECCIÓN: Usar el getter de Lombok .getCantidad()
            CarritoDTO carritoActualizado = carritoService.actualizarCantidad(detalleId, cantidadDto.getCantidad(), userId);

            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            // Captura Detalle no encontrado o acceso denegado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    // 🛑 4. ELIMINAR UN DETALLE (ÍTEM) DEL CARRITO
    @DeleteMapping("/eliminar-detalle/{detalleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> eliminarDetalle(@PathVariable Integer detalleId) {
        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            CarritoDTO carritoActualizado = carritoService.eliminarDetalle(detalleId, userId);

            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            // Captura Detalle no encontrado o acceso denegado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    // 🛑 5. VACIAR EL CARRITO COMPLETO
    @DeleteMapping("/vaciar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> vaciarCarrito() {
        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            CarritoDTO carritoVaciado = carritoService.vaciarCarrito(userId);

            return ResponseEntity.ok(carritoVaciado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ---------------------- Métodos CRUD (Acceso solo Admin) ----------------------

    // 6. LISTAR TODOS LOS CARRITOS (Solo ADMIN)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarritoDTO>> listarTodos() {
        return ResponseEntity.ok(carritoService.listarTodos());
    }

    // 7. ELIMINAR CARRITO POR ID (Solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            carritoService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            // Capturamos el error si el ID no existe
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}

