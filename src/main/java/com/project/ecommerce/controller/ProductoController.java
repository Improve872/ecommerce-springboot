package com.project.ecommerce.controller;

import com.project.ecommerce.dto.ProductoDTO;
import com.project.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; //
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos") //
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 1. GET /productos?category=...&search=... (PÚBLICO)
    // El listado, filtrado y búsqueda deben ser accesibles para todos.
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodosOFiltrar(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<ProductoDTO> productos = productoService.getFilteredProducts(category, search);
        return ResponseEntity.ok(productos);
    }

    // 2. GET /productos/{id} (PÚBLICO)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            ProductoDTO dto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // 3. POST /productos (PROTEGIDO: Solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔒 RESTRICCIÓN DE SEGURIDAD
    public ResponseEntity<ProductoDTO> guardar(@RequestBody ProductoDTO dto) {
        try {
            ProductoDTO savedDto = productoService.guardar(dto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            // Captura errores si la Categoría ID no existe (lanzado por el servicio)
            // Usamos 400 ya que el dato de entrada (categoría ID) es incorrecto.
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    // 4. PUT /productos/{id} (PROTEGIDO: Solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 RESTRICCIÓN DE SEGURIDAD
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id, @RequestBody ProductoDTO dto) {
        try {
            ProductoDTO updatedDto = productoService.actualizar(id, dto);
            return ResponseEntity.ok(updatedDto); // 200 OK
        } catch (RuntimeException e) {
            // Captura errores si el Producto ID o la Categoría ID no existen
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 5. DELETE /productos/{id} (PROTEGIDO: Solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 RESTRICCIÓN DE SEGURIDAD
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found (si el producto no existe)
        }
    }
}

