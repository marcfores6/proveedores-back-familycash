package es.familycash.proveedores.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "proveedor_imagen")
public class ProveedorImagenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JsonBackReference
    private ProveedorEntity proveedor;
}

