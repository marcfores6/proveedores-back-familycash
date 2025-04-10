package es.familycash.proveedores.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoDocumentoEntity;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.repository.ProductoRepository;
import es.familycash.proveedores.repository.ProveedorRepository;
import es.familycash.proveedores.service.AuthService;
import es.familycash.proveedores.service.ProductoService;
import es.familycash.proveedores.service.ProveedorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoService oProductoService;

    @Autowired
    ProveedorService oProveedorService;

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    HttpServletRequest oHttpServletRequest;

    @Autowired
    AuthService oAuthService;

    @GetMapping("")
    public ResponseEntity<Page<ProductoEntity>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oProductoService.getPage(oPageable, filter), HttpStatus.OK);
    }

    @GetMapping("/pagebyproveedor")
    public ResponseEntity<Page<ProductoEntity>> getPageByProveedor(Pageable pageable, HttpServletRequest request) {
        String proveedorId = (String) request.getAttribute("proveedorId");
        Page<ProductoEntity> page = oProductoService.getPageByProveedor(pageable, proveedorId);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> get(@PathVariable Long id) {
        return new ResponseEntity<>(oProductoService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(oProductoService.count(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return new ResponseEntity<>(oProductoService.delete(id), HttpStatus.OK);
    }

    @DeleteMapping("/imagen/{id}")
    public ResponseEntity<?> deleteImagen(@PathVariable Long id) {
        oProductoService.deleteImagen(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoEntity> create(
            @RequestParam("id") Long id,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "descripcionTic", required = false) String descripcionTic,
            @RequestParam(value = "departamento", required = false) Integer departamento,
            @RequestParam(value = "familia", required = false) Integer familia,
            @RequestParam(value = "subfamilia", required = false) Integer subfamilia,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "unidadDeMedida", required = false) String unidadDeMedida,
            @RequestParam(value = "cantidad", required = false) BigDecimal cantidad,
            @RequestParam(value = "centralizado", required = false) String centralizado,
            @RequestParam(value = "apeso", required = false) Integer apeso,
            @RequestParam(value = "unidadDeCaja", required = false) Integer unidadDeCaja,
            @RequestParam(value = "unidadDeServicio", required = false) Integer unidadDeServicio,
            @RequestParam(value = "pk", required = false) Integer pk,
            @RequestParam(value = "cajasCapa", required = false) Integer cajasCapa,
            @RequestParam(value = "cajasPalet", required = false) Integer cajasPalet,
            @RequestParam(value = "proveedor", required = false) String proveedor,
            @RequestParam(value = "referenciaProveedor", required = false) String referenciaProveedor,
            @RequestParam(value = "ean", required = false) String ean,
            @RequestParam(value = "ean_c", required = false) String ean_c,
            @RequestParam(value = "ean_p", required = false) String ean_p,
            @RequestParam(value = "largo", required = false) Integer largo,
            @RequestParam(value = "ancho", required = false) Integer ancho,
            @RequestParam(value = "alto", required = false) Integer alto,
            @RequestParam(value = "peso", required = false) BigDecimal peso,
            @RequestParam(value = "diasCaducidad", required = false) Integer diasCaducidad,
            @RequestParam(value = "ara_cen", required = false) String ara_cen,
            @RequestParam(value = "iva", required = false) String iva,
            @RequestParam(value = "precioVenta", required = false) BigDecimal precioVenta,
            @RequestParam(value = "pvp_hom", required = false) BigDecimal pvp_hom,
            @RequestParam(value = "pvp_and", required = false) BigDecimal pvp_and,
            @RequestParam(value = "pvp_cat", required = false) BigDecimal pvp_cat,
            @RequestParam(value = "precioTarifa", required = false) BigDecimal precioTarifa,
            @RequestParam(value = "pro_fac", required = false) String pro_fac,
            @RequestParam(value = "precioNeto", required = false) BigDecimal precioNeto,
            @RequestParam(value = "pro_ffac", required = false) String pro_ffac,
            @RequestParam(value = "pro_neton", required = false) BigDecimal pro_neton,
            @RequestParam(value = "art_mkd", required = false) String art_mkd,
            @RequestParam(value = "articuloSustituido", required = false) String articuloSustituido,
            @RequestParam(value = "insertedBy", required = false) String insertedBy,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "imagen", required = false) String imagen,
            @RequestParam(value = "partidaArancelaria", required = false) String partidaArancelaria,
            @RequestParam(value = "pvp_mel", required = false) BigDecimal pvp_mel,
            @RequestParam(value = "paisOrigen", required = false) String paisOrigen,
            @RequestParam(value = "imagenUrls", required = false) List<String> imagenUrls,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes) throws IOException {
        ProductoEntity producto = new ProductoEntity();

        producto.setId(id);
        producto.setDescripcion(descripcion);
        producto.setDescripcionTic(descripcionTic);
        producto.setDepartamento(departamento);
        producto.setFamilia(familia);
        producto.setSubfamilia(subfamilia);
        producto.setMarca(marca);
        producto.setUnidadDeMedida(unidadDeMedida);
        producto.setCantidad(cantidad);
        producto.setCentralizado(centralizado);
        producto.setApeso(apeso);
        producto.setUnidadDeCaja(unidadDeCaja);
        producto.setUnidadDeServicio(unidadDeServicio);
        producto.setPk(pk);
        producto.setCajasCapa(cajasCapa);
        producto.setCajasPalet(cajasPalet);
        producto.setProveedor(proveedor);
        producto.setReferenciaProveedor(referenciaProveedor);
        producto.setEan(ean);
        producto.setEan_c(ean_c);
        producto.setEan_p(ean_p);
        producto.setLargo(largo);
        producto.setAncho(ancho);
        producto.setAlto(alto);
        producto.setPeso(peso);
        producto.setDiasCaducidad(diasCaducidad);
        producto.setAra_cen(ara_cen);
        producto.setIva(iva);
        producto.setPrecioVenta(precioVenta);
        producto.setPvp_hom(pvp_hom);
        producto.setPvp_and(pvp_and);
        producto.setPvp_cat(pvp_cat);
        producto.setPrecioTarifa(precioTarifa);
        producto.setPro_fac(pro_fac);
        producto.setPrecioNeto(precioNeto);
        producto.setPro_ffac(pro_ffac);
        producto.setPro_neton(pro_neton);
        producto.setArt_mkd(art_mkd);
        producto.setArticuloSustituido(articuloSustituido);
        producto.setInsertedBy(insertedBy);
        producto.setUpdateBy(updateBy);
        producto.setStatus(status);
        producto.setObservaciones(observaciones);
        producto.setImagen(imagen);
        producto.setPartidaArancelaria(partidaArancelaria);
        producto.setPvp_mel(pvp_mel);
        producto.setPaisOrigen(paisOrigen);

        // Aquí procesas las imágenes y las URLs
        ProductoEntity saved = oProductoService.create(producto, imagenes, imagenUrls);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoEntity> update(
            @PathVariable Long id,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "descripcionTic", required = false) String descripcionTic,
            @RequestParam(value = "departamento", required = false) Integer departamento,
            @RequestParam(value = "familia", required = false) Integer familia,
            @RequestParam(value = "subfamilia", required = false) Integer subfamilia,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "unidadDeMedida", required = false) String unidadDeMedida,
            @RequestParam(value = "cantidad", required = false) BigDecimal cantidad,
            @RequestParam(value = "centralizado", required = false) String centralizado,
            @RequestParam(value = "apeso", required = false) Integer apeso,
            @RequestParam(value = "unidadDeCaja", required = false) Integer unidadDeCaja,
            @RequestParam(value = "unidadDeServicio", required = false) Integer unidadDeServicio,
            @RequestParam(value = "pk", required = false) Integer pk,
            @RequestParam(value = "cajasCapa", required = false) Integer cajasCapa,
            @RequestParam(value = "cajasPalet", required = false) Integer cajasPalet,
            @RequestParam(value = "proveedor", required = false) String proveedor,
            @RequestParam(value = "referenciaProveedor", required = false) String referenciaProveedor,
            @RequestParam(value = "ean", required = false) String ean,
            @RequestParam(value = "ean_c", required = false) String ean_c,
            @RequestParam(value = "ean_p", required = false) String ean_p,
            @RequestParam(value = "largo", required = false) Integer largo,
            @RequestParam(value = "ancho", required = false) Integer ancho,
            @RequestParam(value = "alto", required = false) Integer alto,
            @RequestParam(value = "peso", required = false) BigDecimal peso,
            @RequestParam(value = "diasCaducidad", required = false) Integer diasCaducidad,
            @RequestParam(value = "ara_cen", required = false) String ara_cen,
            @RequestParam(value = "iva", required = false) String iva,
            @RequestParam(value = "precioVenta", required = false) BigDecimal precioVenta,
            @RequestParam(value = "pvp_hom", required = false) BigDecimal pvp_hom,
            @RequestParam(value = "pvp_and", required = false) BigDecimal pvp_and,
            @RequestParam(value = "pvp_cat", required = false) BigDecimal pvp_cat,
            @RequestParam(value = "precioTarifa", required = false) BigDecimal precioTarifa,
            @RequestParam(value = "pro_fac", required = false) String pro_fac,
            @RequestParam(value = "precioNeto", required = false) BigDecimal precioNeto,
            @RequestParam(value = "pro_ffac", required = false) String pro_ffac,
            @RequestParam(value = "pro_neton", required = false) BigDecimal pro_neton,
            @RequestParam(value = "art_mkd", required = false) String art_mkd,
            @RequestParam(value = "articuloSustituido", required = false) String articuloSustituido,
            @RequestParam(value = "insertedBy", required = false) String insertedBy,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "imagen", required = false) String imagen,
            @RequestParam(value = "partidaArancelaria", required = false) String partidaArancelaria,
            @RequestParam(value = "pvp_mel", required = false) BigDecimal pvp_mel,
            @RequestParam(value = "paisOrigen", required = false) String paisOrigen,
            @RequestParam(value = "insertedAt", required = false) String insertedAtStr,
            @RequestParam(value = "updateAt", required = false) String updateAtStr,
            @RequestParam(value = "imagenUrls", required = false) List<String> imagenUrls,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestPart(value = "documentos", required = false) List<MultipartFile> documentos) throws IOException {
        ProductoEntity producto = new ProductoEntity();

        producto.setId(id);
        producto.setDescripcion(descripcion);
        producto.setDescripcionTic(descripcionTic);
        producto.setDepartamento(departamento);
        producto.setFamilia(familia);
        producto.setSubfamilia(subfamilia);
        producto.setMarca(marca);
        producto.setUnidadDeMedida(unidadDeMedida);
        producto.setCantidad(cantidad);
        producto.setCentralizado(centralizado);
        producto.setApeso(apeso);
        producto.setUnidadDeCaja(unidadDeCaja);
        producto.setUnidadDeServicio(unidadDeServicio);
        producto.setPk(pk);
        producto.setCajasCapa(cajasCapa);
        producto.setCajasPalet(cajasPalet);
        producto.setProveedor(proveedor);
        producto.setReferenciaProveedor(referenciaProveedor);
        producto.setEan(ean);
        producto.setEan_c(ean_c);
        producto.setEan_p(ean_p);
        producto.setLargo(largo);
        producto.setAncho(ancho);
        producto.setAlto(alto);
        producto.setPeso(peso);
        producto.setDiasCaducidad(diasCaducidad);
        producto.setAra_cen(ara_cen);
        producto.setIva(iva);
        producto.setPrecioVenta(precioVenta);
        producto.setPvp_hom(pvp_hom);
        producto.setPvp_and(pvp_and);
        producto.setPvp_cat(pvp_cat);
        producto.setPrecioTarifa(precioTarifa);
        producto.setPro_fac(pro_fac);
        producto.setPrecioNeto(precioNeto);
        producto.setPro_ffac(pro_ffac);
        producto.setPro_neton(pro_neton);
        producto.setArt_mkd(art_mkd);
        producto.setArticuloSustituido(articuloSustituido);
        producto.setInsertedBy(insertedBy);
        producto.setUpdateBy(updateBy);
        producto.setStatus(status);
        producto.setObservaciones(observaciones);
        producto.setImagen(imagen);
        producto.setPartidaArancelaria(partidaArancelaria);
        producto.setPvp_mel(pvp_mel);
        producto.setPaisOrigen(paisOrigen);

        if (insertedAtStr != null && !insertedAtStr.isEmpty()) {
            producto.setInsertedAt(Timestamp.from(Instant.parse(insertedAtStr)));
        }

        if (updateAtStr != null && !updateAtStr.isEmpty()) {
            producto.setUpdateAt(Timestamp.from(Instant.parse(updateAtStr)));
        }

        ProductoEntity updated = oProductoService.update(producto, imagenes, imagenUrls);

        ProductoEntity productoCompleto = oProductoService.findById(id);
        oProductoService.guardarDocumentosDelProducto(productoCompleto, documentos);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocumentos(
            @PathVariable Long id,
            @RequestPart("documentos") List<MultipartFile> documentos) {
        try {
            ProductoEntity producto = oProductoService.findById(id);
            oProductoService.guardarDocumentosDelProducto(producto, documentos);
            return ResponseEntity.ok("Documentos subidos correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir los documentos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/documentos")
    public ResponseEntity<List<ProductoDocumentoEntity>> getDocumentosByProducto(@PathVariable Long id) {
        List<ProductoDocumentoEntity> documentos = oProductoService.obtenerDocumentosDeProducto(id);
        return ResponseEntity.ok(documentos);
    }

    @DeleteMapping("/documento/{id}")
    public ResponseEntity<?> eliminarDocumento(@PathVariable Long id) {
        try {
            oProductoService.eliminarDocumento(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el archivo físico.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/enviar")
    public ResponseEntity<String> enviarProducto(@PathVariable Long id) {
        try {
            ProductoEntity producto = oProductoService.enviarProducto(id);
            return ResponseEntity.ok("Producto enviado correctamente"); // Devolver texto plano
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el producto: " + e.getMessage());
        }
    }

}
