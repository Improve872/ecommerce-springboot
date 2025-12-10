package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CarritoDetalleDTO;
import com.project.ecommerce.service.CarritoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrito-detalles")
public class CarritoDetalleController {

    @Autowired
    private CarritoDetalleService detalleService;

    // POST /carrito-detalles/agregar
    // ⚠️ Usa el método de lógica de negocio para AGREGAR O ACTUALIZAR (suma la cantidad)
    @PostMapping("/agregar")
    public ResponseEntity<CarritoDetalleDTO> agregarOActualizarItem(@RequestBody CarritoDetalleDTO dto) {
        try {
            // El servicio maneja si es una suma o una nueva línea
            CarritoDetalleDTO savedDto = detalleService.agregarOActualizarItem(dto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            // Captura errores si Carrito o Producto ID no existen
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    // PUT /carrito-detalles/{id}/cantidad?value=...
    // ⚠️ Usa el método de lógica de negocio para MODIFICAR CANTIDAD (incluye eliminación si es 0)
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<CarritoDetalleDTO> modificarCantidad(
            @PathVariable Integer id,
            @RequestParam Integer value) {
        try {
            CarritoDetalleDTO updatedDto = detalleService.modificarCantidad(id, value);

            // Si el servicio devuelve null (cantidad <= 0), significa que el ítem fue eliminado
            if (updatedDto == null) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }

            return ResponseEntity.ok(updatedDto); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // ---------------------- Métodos CRUD (Acceso solo Admin) ----------------------

    // GET /carrito-detalles
    @GetMapping
    public List<CarritoDetalleDTO> listarTodos() {
        return detalleService.listarTodos();
    }

    // GET /carrito-detalles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CarritoDetalleDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            CarritoDetalleDTO dto = detalleService.obtenerPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // DELETE /carrito-detalles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

