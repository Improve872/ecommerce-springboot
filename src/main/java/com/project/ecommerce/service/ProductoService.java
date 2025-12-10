package com.project.ecommerce.service;

import com.project.ecommerce.dto.ProductoDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.Categoria;
import com.project.ecommerce.model.Producto;
import com.project.ecommerce.repository.CategoriaRepository;
import com.project.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🛑 ¡NUEVO IMPORT!

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // metodo para listar sin filtros
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(Mapper::toProductoDTO)
                .collect(Collectors.toList());
    }

    // metodo parar la logica de filtrado
    public List<ProductoDTO> getFilteredProducts(String category, String search) {

        List<Producto> products;

        if (category != null && !category.isEmpty()) {
            products = productoRepository.findByCategoryEntityNameIgnoreCase(category);

        } else if (search != null && !search.isEmpty()) {
            products = productoRepository.findByNameContainingIgnoreCaseOrDescripcionContainingIgnoreCase(search, search);

        } else {
            // Si no hay filtros, se devuelve el listado completo (igual que listarTodos)
            products = productoRepository.findAll();
        }

        return products.stream()
                .map(Mapper::toProductoDTO)
                .collect(Collectors.toList());
    }


    public ProductoDTO obtenerPorId(Integer id) {
        return productoRepository.findById(id)
                .map(Mapper::toProductoDTO)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // metodos post put delete

    @Transactional
    public ProductoDTO guardar(ProductoDTO dto) {

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no válida con ID: " + dto.getCategoriaId()));

        Producto producto = Mapper.toProductoEntity(dto, categoria);
        Producto savedProduct = productoRepository.save(producto);

        // devolver dto
        return Mapper.toProductoDTO(savedProduct);
    }

    @Transactional
    public ProductoDTO actualizar(Integer id, ProductoDTO dto) {

        // validar que la  categoria exista
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no válida con ID: " + dto.getCategoriaId()));

        return productoRepository.findById(id).map(producto -> {

            // actualizar campos
            producto.setName(dto.getName());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecio(dto.getPrecio());
            producto.setStock(dto.getStock());
            producto.setDisponible(dto.getDisponible());
            producto.setImageUrl(dto.getImageUrl());
            producto.setCategoryEntity(categoria); // 🔗 Asignar la FK

            // guardar y devolver dto
            Producto updatedProduct = productoRepository.save(producto);
            return Mapper.toProductoDTO(updatedProduct);

        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}


