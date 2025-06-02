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


@Entity
@Table(name = "DT_ARA_IMG_DES")
public class ProductoImagenEntityDes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "ara_id")
    @JsonBackReference
    private ProductoEntityDes producto;

    @Column(name = "imagenUrl")
    private String imagenUrl; // Si se usa URL para las imágenes

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoEntityDes getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntityDes producto) {
        this.producto = producto;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }


    // Getters and setters

}
 
