package com.project.ecommerce.repository;

import com.project.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // 1. Método para filtrar por el nombre de la Categoría
    // Nota: 'CategoryEntity' es el nombre del campo de la entidad Categoria dentro de Producto.java
    //       'Name' es el nombre del campo dentro de la entidad Categoria.
    List<Producto> findByCategoryEntityNameIgnoreCase(String name);

    // 2. Método para búsqueda por palabra clave (busca en nombre O descripción)
    List<Producto> findByNameContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String name, String descripcion);
}
