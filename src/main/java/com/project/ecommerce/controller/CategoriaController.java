package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CategoriaDTO;
import com.project.ecommerce.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🛑 ¡IMPORTAR ESTO!
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// URL Base: /api/v1/categorias
// Nota: La base /api/v1/ se define generalmente en application.properties o en la clase principal.
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // 1. OBTENER TODOS (PÚBLICO)
    // No necesita @PreAuthorize, ya que permitimos el acceso a /categorias/** en SecurityConfig
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodos() {
        // En tu implementación original no devolvías ResponseEntity,
        // pero es buena práctica devolverlo para control HTTP 200 OK.
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    // 2. OBTENER POR ID (PÚBLICO)
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            CategoriaDTO dto = categoriaService.obtenerPorId(id);
            return ResponseEntity.ok(dto); // 200 OK
        } catch (RuntimeException e) {
            // Manejamos la excepción lanzada por el servicio
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // 3. CREAR CATEGORÍA (PROTEGIDO: Solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔒 Solo el rol ADMIN puede acceder
    public ResponseEntity<CategoriaDTO> guardar(@RequestBody CategoriaDTO dto) {
        CategoriaDTO savedDto = categoriaService.guardar(dto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED); // 201 Created
    }

    // 4. ACTUALIZAR CATEGORÍA (PROTEGIDO: Solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 Solo el rol ADMIN puede acceder
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Integer id, @RequestBody CategoriaDTO dto) {
        try {
            CategoriaDTO updated = categoriaService.actualizar(id, dto);
            return ResponseEntity.ok(updated); // 200 OK
        } catch (RuntimeException e) {
            // Manejamos la excepción lanzada por el servicio
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // 5. ELIMINAR CATEGORÍA (PROTEGIDO: Solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 Solo el rol ADMIN puede acceder
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        // Usamos try/catch para manejar la excepción si el ID no existe
        try {
            categoriaService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            // Si hay un error (ej. el ID no existe o no se pudo eliminar)
            return ResponseEntity.notFound().build();
        }
    }
}


