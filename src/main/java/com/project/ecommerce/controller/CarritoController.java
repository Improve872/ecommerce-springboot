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

    //  obtener carrito de usuario logueado
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

    // añadir actualizar producto
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

    // actualizar cantidad de un detalle
    @PutMapping("/actualizar/{detalleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Integer detalleId,
            @RequestBody CantidadDTO cantidadDto) {

        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);


            CarritoDTO carritoActualizado = carritoService.actualizarCantidad(detalleId, cantidadDto.getCantidad(), userId);

            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // eliminar un detalle
    @DeleteMapping("/eliminar-detalle/{detalleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarritoDTO> eliminarDetalle(@PathVariable Integer detalleId) {
        try {
            String userEmail = securityUtils.getCurrentUserEmail();
            Integer userId = usuarioService.getUserIdByEmail(userEmail);

            CarritoDTO carritoActualizado = carritoService.eliminarDetalle(detalleId, userId);

            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    // vaciar el carrito
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


    //  Métodos CRUD acceso solo admin

    // listar todos los carritos
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarritoDTO>> listarTodos() {
        return ResponseEntity.ok(carritoService.listarTodos());
    }

    // eliminar carrito por id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            carritoService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build(); // 404
        }
    }
}

