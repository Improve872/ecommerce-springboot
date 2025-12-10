package com.project.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder; // Asegúrate de tener este import
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer idCarrito;

    // 🛑 EAGER: Necesario para que el Mapper pueda obtener el ID del usuario fuera de la transacción.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    @JsonBackReference
    private Usuario usuario;

    // 🛑 @Builder.Default: CRÍTICO. Garantiza que el valor 'true' se use con el builder.
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // 🛑 @Builder.Default: CRÍTICO. Garantiza que la fecha se use con el builder.
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // 🛑 EAGER y @Builder.Default: Necesario para evitar LazyException y NullPointerException al mapear DTO.
    @Builder.Default
    @OneToMany(
            mappedBy = "carrito",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<CarritoDetalle> detalles = new ArrayList<>();

    // El @PrePersist ya no es estrictamente necesario gracias a @Builder.Default,
    // pero puede dejarse como doble seguridad.
    @PrePersist
    protected void onCreate() {
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}

