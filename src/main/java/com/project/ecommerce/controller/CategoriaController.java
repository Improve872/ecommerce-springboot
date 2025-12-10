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

@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // obtener todos
    // no necesita @PreAuthorize
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodos() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    // obtener por id
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

    // crear categoria solo admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔒 Solo el rol ADMIN puede acceder
    public ResponseEntity<CategoriaDTO> guardar(@RequestBody CategoriaDTO dto) {
        CategoriaDTO savedDto = categoriaService.guardar(dto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED); // 201 Created
    }

    // actualizar categoria solo admin
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

    // eliminar categoria solo admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 Solo el rol ADMIN puede acceder
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        // por si el id no existe
        try {
            categoriaService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {

            return ResponseEntity.notFound().build();
        }
    }
}


