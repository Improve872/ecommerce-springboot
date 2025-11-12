package com.project.ecommerce.controller;

import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.service.CarritoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carrito-detalles")
public class CarritoDetalleController {

    @Autowired
    private CarritoDetalleService carritoDetalleService;

    @GetMapping
    public List<CarritoDetalle> listarDetalles() {
        return carritoDetalleService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<CarritoDetalle> obtenerDetalle(@PathVariable Integer id) {
        return carritoDetalleService.obtenerPorId(id);
    }

    @PostMapping
    public CarritoDetalle guardarDetalle(@RequestBody CarritoDetalle detalle) {
        return carritoDetalleService.guardar(detalle);
    }

    @DeleteMapping("/{id}")
    public void eliminarDetalle(@PathVariable Integer id) {
        carritoDetalleService.eliminar(id);
    }
}
