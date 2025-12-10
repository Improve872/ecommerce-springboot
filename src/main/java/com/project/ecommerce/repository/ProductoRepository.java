package com.project.ecommerce.repository;

import com.project.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // metodo para filtrar por el nombre de categoria

    List<Producto> findByCategoryEntityNameIgnoreCase(String name);

    // metodo para buscar por palabra clave
    List<Producto> findByNameContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String name, String descripcion);
}
