package com.project.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.ecommerce.model.Carrito;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {


    // buscar al carrito asociado al id del usuario

    Optional<Carrito> findByUsuarioId(Integer usuarioId);


}
