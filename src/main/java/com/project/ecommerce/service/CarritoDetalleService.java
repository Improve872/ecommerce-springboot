package com.project.ecommerce.service;

import com.project.ecommerce.model.CarritoDetalle;
import com.project.ecommerce.repository.CarritoDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    public List<CarritoDetalle> listarTodos() {
        return carritoDetalleRepository.findAll();
    }

    public Optional<CarritoDetalle> obtenerPorId(Integer id) {
        return carritoDetalleRepository.findById(id);
    }

    public CarritoDetalle guardar(CarritoDetalle detalle) {
        return carritoDetalleRepository.save(detalle);
    }

    public void eliminar(Integer id) {
        carritoDetalleRepository.deleteById(id);
    }
}
