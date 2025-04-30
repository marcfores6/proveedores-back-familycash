package es.familycash.proveedores.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LU_ARA")
public class ProductoEntity {

    @Id
    @Column(name = "ara_id")
    private Long id;

    @Column(name = "ara_ds")
    private String descripcion;

    @Column(name = "ara_marca")
    private String marca;

    @Column(name = "ara_udm_id")
    private String unidadDeMedida;

    @Column(name = "ara_cent")
    private String centralizado;

    @Column(name = "ara_uc")
    private Integer unidadDeCaja;

    @Column(name = "ara_us")
    private Integer unidadDeServicio;

    @Column(name = "ara_pk")
    private Integer unidadDePack;// PREGUNTAR;

    @Column(name = "ara_cajas_capa")
    private Integer cajasCapa;

    @Column(name = "ara_cajas_palet")
    private Integer cajasPalet;

    @Column(name = "ara_pro_ref")
    private String referenciaProveedor;

    @Column(name = "ara_ean")
    private String ean;

    @Column(name = "ara_ean_c")
    private String ean_caja; // PREGUNTAR;

    @Column(name = "ara_ean_p")
    private String ean_pack;// PREGUNTAR;

    @Column(name = "ara_largo_c")
    private Integer largo_caja;

    @Column(name = "ara_ancho_c")
    private Integer ancho_caja;

    @Column(name = "ara_alto_c")
    private Integer alto_caja;

    @Column(name = "ara_peso_c", precision = 7, scale = 3)
    private BigDecimal peso_caja;

    @Column(name = "ara_dias_cad")
    private Integer diasCaducidad;

    @Column(name = "ara_iva")
    private String iva;

    @Column(name = "ARA_PRO")
    private String proveedor;// PREGUNTAR

    @Column(name = "ara_obs")
    private String observaciones;

    @Column(name = "ara_image")
    private String imagen;

    @Column(name = "ara_part_arancel")
    private String partidaArancelaria;

    @Column(name = "pais_origen")
    private String paisOrigen;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductoImagenEntity> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductoDocumentoEntity> documentos = new ArrayList<>();

    @Column(name = "estado")
    private String estado;

}
