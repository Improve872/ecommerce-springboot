package com.project.ecommerce.controller;

import com.project.ecommerce.model.Producto;
import com.project.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarTodos(); // ✅ nombre corregido
    }

    @GetMapping("/{id}")
    public Optional<Producto> obtenerProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id);
    }

    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto); // ✅ nombre corregido
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Integer id) {
        productoService.eliminar(id);
    }
}