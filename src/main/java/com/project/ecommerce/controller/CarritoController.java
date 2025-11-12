package com.project.ecommerce.controller;

import com.project.ecommerce.model.Carrito;
import com.project.ecommerce.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public List<Carrito> listarCarritos() {
        return carritoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Carrito> obtenerCarrito(@PathVariable Integer id) {
        return carritoService.obtenerPorId(id);
    }

    @PostMapping
    public Carrito guardarCarrito(@RequestBody Carrito carrito) {
        return carritoService.guardar(carrito);
    }

    @DeleteMapping("/{id}")
    public void eliminarCarrito(@PathVariable Integer id) {
        carritoService.eliminar(id);
    }
}
