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

    // listado filtrar busqueda deben ser accesibles para todos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodosOFiltrar(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<ProductoDTO> productos = productoService.getFilteredProducts(category, search);
        return ResponseEntity.ok(productos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            ProductoDTO dto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // solo admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> guardar(@RequestBody ProductoDTO dto) {
        try {
            ProductoDTO savedDto = productoService.guardar(dto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // si la Categoría id no existe
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // solo admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") //
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id, @RequestBody ProductoDTO dto) {
        try {
            ProductoDTO updatedDto = productoService.actualizar(id, dto);
            return ResponseEntity.ok(updatedDto);
        } catch (RuntimeException e) {
            //  si el producto id o la categoria id no existen
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // solo admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 🔒 RESTRICCIÓN DE SEGURIDAD
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

