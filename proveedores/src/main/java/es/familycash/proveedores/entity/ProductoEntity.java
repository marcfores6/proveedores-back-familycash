package es.familycash.proveedores.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @Column(name = "ara_ds_tic")
    private String descripcionTic;

    @Column(name = "ara_dpt")
    private Integer departamento;

    @Column(name = "ara_fam")
    private Integer familia;

    @Column(name = "ara_sbf")
    private Integer subfamilia;

    @Column(name = "ara_marca")
    private String marca;

    @Column(name = "ara_udm_id")
    private String unidadDeMedida;

    @Column(name = "ara_udm_ctd", precision = 7, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "ara_cent")
    private String centralizado;

    @Column(name = "ara_apeso")
    private Integer apeso; // PREGUNTAR;

    @Column(name = "ara_uc")
    private Integer unidadDeCaja;

    @Column(name = "ara_us")
    private Integer unidadDeServicio;

    @Column(name = "ara_pk")
    private Integer pk;// PREGUNTAR;

    @Column(name = "ara_cajas_capa")
    private Integer cajasCapa;

    @Column(name = "ara_cajas_palet")
    private Integer cajasPalet;

    @Column(name = "ARA_PRO")
    private String proveedor;// PREGUNTAR;

    @Column(name = "ara_pro_ref")
    private String referenciaProveedor;

    @Column(name = "ara_ean")
    private String ean;

    @Column(name = "ara_ean_c")
    private String ean_c; // PREGUNTAR;

    @Column(name = "ara_ean_p")
    private String ean_p;// PREGUNTAR;

    @Column(name = "ara_largo_c")
    private Integer largo;

    @Column(name = "ara_ancho_c")
    private Integer ancho;

    @Column(name = "ara_alto_c")
    private Integer alto;

    @Column(name = "ara_peso_c", precision = 7, scale = 3)
    private BigDecimal peso;

    @Column(name = "ara_dias_cad")
    private Integer diasCaducidad;

    @Column(name = "ara_cen")
    private String ara_cen;// PREGUNTAR;

    @Column(name = "ara_iva")
    private String iva;

    @Column(name = "ara_pvp", precision = 12, scale = 4)
    private BigDecimal precioVenta;

    @Column(name = "ara_pvp_hom", precision = 12, scale = 4)
    private BigDecimal pvp_hom;// PREGUNTAR;

    @Column(name = "ara_pvp_and", precision = 12, scale = 4)
    private BigDecimal pvp_and;// PREGUNTAR;

    @Column(name = "ara_pvp_cat", precision = 12, scale = 4)
    private BigDecimal pvp_cat;// PREGUNTAR;

    @Column(name = "ara_pro_tar", precision = 12, scale = 4)
    private BigDecimal precioTarifa;

    @Column(name = "ara_pro_fac")
    private String pro_fac;// PREGUNTAR;

    @Column(name = "ara_pro_neto", precision = 12, scale = 4)
    private BigDecimal precioNeto;

    @Column(name = "ara_pro_ffac")
    private String pro_ffac;// PREGUNTAR;

    @Column(name = "ara_pro_neton", precision = 12, scale = 4)
    private BigDecimal pro_neton;// PREGUNTAR;

    @Column(name = "ara_art_mkd")
    private String art_mkd;// PREGUNTAR;

    @Column(name = "ara_art_sust")
    private String articuloSustituido;

    @Column(name = "insert_by")
    private String insertedBy;

    @Column(name = "insert_at")
    private Timestamp insertedAt;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_at")
    private Timestamp updateAt;

    @Column(name = "ara_status")
    private Integer status;

    @Column(name = "ara_obs")
    private String observaciones;

    @Column(name = "ara_image")
    private String imagen;

    @Column(name = "ara_part_arancel")
    private String partidaArancelaria;

    @Column(name = "ara_pvp_mel", precision = 12, scale = 4)
    private BigDecimal pvp_mel;// PREGUNTAR;

    @Column(name = "pais_origen")
    private String paisOrigen;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductoImagenEntity> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoDocumentoEntity> documentos = new ArrayList<>();

}
