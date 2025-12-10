package com.project.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id; // Usamos 'id' en Java

    // ⚠️ Mapeamos la columna 'nombre' de la BD a la propiedad 'name'
    @Column(name = "nombre")
    private String name;

    private String descripcion;

    // Si tu tabla no tiene más campos, este modelo está listo.
    // Si tu tabla tiene la columna 'descripcion', esta línea es correcta.
}