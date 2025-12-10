package com.project.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.ecommerce.model.Carrito;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {


    // Buscar el carrito asociado al ID de usuario.

    Optional<Carrito> findByUsuarioId(Integer usuarioId);


}
