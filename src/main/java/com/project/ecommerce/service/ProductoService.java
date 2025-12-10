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

    // --- Métodos de Lectura (GET) ---

    // 🛑 Método principal para listar (sin filtros)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(Mapper::toProductoDTO)
                .collect(Collectors.toList());
    }

    // Método para la lógica de filtrado (usado por el controlador)
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

    // --- Métodos de Modificación (POST, PUT, DELETE) ---

    @Transactional // ✅ Asegura que la operación sea atómica (o se completa o se revierte)
    public ProductoDTO guardar(ProductoDTO dto) {
        // 1. Validar y Obtener la Categoria (Falla con 404 si no existe)
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no válida con ID: " + dto.getCategoriaId()));

        // 2. Mapear, usar la Categoria obtenida para la relación
        Producto producto = Mapper.toProductoEntity(dto, categoria);
        Producto savedProduct = productoRepository.save(producto);

        // 3. Devolver DTO
        return Mapper.toProductoDTO(savedProduct);
    }

    @Transactional // ✅ Asegura que la operación sea atómica
    public ProductoDTO actualizar(Integer id, ProductoDTO dto) {

        // 1. Validar que la categoría exista antes de intentar actualizar
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no válida con ID: " + dto.getCategoriaId()));

        return productoRepository.findById(id).map(producto -> {

            // 2. Actualizar campos
            producto.setName(dto.getName());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecio(dto.getPrecio());
            producto.setStock(dto.getStock());
            producto.setDisponible(dto.getDisponible());
            producto.setImageUrl(dto.getImageUrl());
            producto.setCategoryEntity(categoria); // 🔗 Asignar la FK

            // 3. Guardar y devolver DTO
            Producto updatedProduct = productoRepository.save(producto);
            return Mapper.toProductoDTO(updatedProduct);

        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional // ✅ Asegura que la operación sea atómica
    public void eliminar(Integer id) {
        // 🛑 Mejorar la gestión de la excepción 404 para el controlador
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}


