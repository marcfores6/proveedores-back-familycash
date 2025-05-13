package es.familycash.proveedores.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DT_PRO_DOC_DES")
@Getter
@Setter
@NoArgsConstructor
public class ProductoDocumentoEntityDes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonBackReference
    private ProductoEntityDes producto;

    @Column(name = "documento_url", nullable = false)
    private String documentoUrl;

    @Column(name = "nombre_original")
    private String nombreOriginal;

    @Column(length = 1)
    private String tipo; // T, N o L

}
