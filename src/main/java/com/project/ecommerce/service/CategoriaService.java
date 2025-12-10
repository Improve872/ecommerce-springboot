package com.project.ecommerce.service;

import com.project.ecommerce.dto.CategoriaDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.Categoria;
import com.project.ecommerce.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(Mapper::toCategoriaDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO obtenerPorId(Integer id) {
        return categoriaRepository.findById(id)
                .map(Mapper::toCategoriaDTO)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    public CategoriaDTO guardar(CategoriaDTO dto) {
        Categoria categoria = Mapper.toCategoriaEntity(dto);
        Categoria saved = categoriaRepository.save(categoria);
        return Mapper.toCategoriaDTO(saved);
    }

    public CategoriaDTO actualizar(Integer id, CategoriaDTO dto) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setName(dto.getName());
                    categoria.setDescripcion(dto.getDescription());
                    Categoria updated = categoriaRepository.save(categoria);
                    return Mapper.toCategoriaDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }
}

