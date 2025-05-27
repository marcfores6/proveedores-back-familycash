package es.familycash.proveedores.api;

import org.springframework.beans.factory.annotation.Autowired;
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
import es.familycash.proveedores.service.ProductoServiceRouter;
import es.familycash.proveedores.service.ProveedorServiceRouter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoServiceRouter oProductoService;

    @Autowired
    ProveedorServiceRouter oProveedorService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        try {
            oProductoService.deleteProductoYArchivos(id);
            return ResponseEntity.ok("Producto eliminado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error eliminando archivos del producto: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error eliminando producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/imagen/{id}")
    public ResponseEntity<?> deleteImagen(@PathVariable Long id) throws IOException {
        oProductoService.deleteImagen(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoEntity> create(
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "unidadDeCaja", required = false) Integer unidadDeCaja,
            @RequestParam(value = "unidadDePack", required = false) Integer unidadDePack,
            @RequestParam(value = "cajasCapa", required = false) Integer cajasCapa,
            @RequestParam(value = "cajasPalet", required = false) Integer cajasPalet,
            @RequestParam(value = "referenciaProveedor", required = false) String referenciaProveedor,
            @RequestParam(value = "ean", required = false) String ean,
            @RequestParam(value = "ean_caja", required = false) String ean_caja,
            @RequestParam(value = "ean_pack", required = false) String ean_pack,
            @RequestParam(value = "largo_caja", required = false) Integer largo_caja,
            @RequestParam(value = "ancho_caja", required = false) Integer ancho_caja,
            @RequestParam(value = "alto_caja", required = false) Integer alto_caja,
            @RequestParam(value = "peso_caja", required = false) BigDecimal peso_caja,
            @RequestParam(value = "largo_unidad", required = false) Integer largo_unidad,
            @RequestParam(value = "ancho_unidad", required = false) Integer ancho_unidad,
            @RequestParam(value = "alto_unidad", required = false) Integer alto_unidad,
            @RequestParam(value = "peso_neto_unidad", required = false) BigDecimal peso_neto_unidad,
            @RequestParam(value = "peso_escurrido_unidad", required = false) BigDecimal peso_escurrido_unidad,
            @RequestParam(value = "diasCaducidad", required = false) Integer diasCaducidad,
            @RequestParam(value = "iva", required = false) String iva,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "imagen", required = false) String imagen,
            @RequestParam(value = "partidaArancelaria", required = false) String partidaArancelaria,
            @RequestParam(value = "paisOrigen", required = false) String paisOrigen,
            @RequestParam(name = "imagenUrls", required = false) List<String> imagenUrls,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestPart(value = "documentos", required = false) List<MultipartFile> documentos,
            @RequestParam(value = "leadtime", required = false) Integer leadtime,
            @RequestParam(value = "moq", required = false) Integer moq,
            @RequestParam(value = "multiplo_de_pedido", required = false) String multiploDePedido,
            @RequestParam(name = "tiposDocumentos", required = false) List<String> tiposDocumentos,
            @RequestParam(value = "proveedor", required = false) String proveedor) throws IOException {

        ProductoEntity producto = new ProductoEntity();
        producto.setDescripcion(descripcion);
        producto.setMarca(marca);
        producto.setUnidadDeCaja(unidadDeCaja);
        producto.setUnidadDePack(unidadDePack);
        producto.setCajasCapa(cajasCapa);
        producto.setCajasPalet(cajasPalet);
        producto.setReferenciaProveedor(referenciaProveedor);
        producto.setEan(ean);
        producto.setEan_caja(ean_caja);
        producto.setEan_pack(ean_pack);
        producto.setLargo_caja(largo_caja);
        producto.setAncho_caja(ancho_caja);
        producto.setAlto_caja(alto_caja);
        producto.setPeso_caja(peso_caja);
        producto.setLargo_unidad(largo_unidad);
        producto.setAncho_unidad(ancho_unidad);
        producto.setAlto_unidad(alto_unidad);
        producto.setPeso_neto_unidad(peso_neto_unidad);
        producto.setPeso_escurrido_unidad(peso_escurrido_unidad);
        producto.setDiasCaducidad(diasCaducidad);
        producto.setIva(iva);
        producto.setObservaciones(observaciones);
        producto.setImagen(imagen);
        producto.setPartidaArancelaria(partidaArancelaria);
        producto.setPaisOrigen(paisOrigen);
        producto.setLeadtime(leadtime);
        producto.setMoq(moq);
        producto.setMultiploDePedido(multiploDePedido);
        producto.setProveedor(proveedor);
        producto.setEstado("PENDIENTE");

        ProductoEntity creado = oProductoService.create(producto, imagenes, imagenUrls);

        if (documentos != null && !documentos.isEmpty()) {
            oProductoService.guardarDocumentosDelProducto(creado, documentos, tiposDocumentos);
        }

        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoEntity> update(
            @PathVariable Long id,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "unidadDeCaja", required = false) Integer unidadDeCaja,
            @RequestParam(value = "unidadDePack", required = false) Integer unidadDePack,
            @RequestParam(value = "cajasCapa", required = false) Integer cajasCapa,
            @RequestParam(value = "cajasPalet", required = false) Integer cajasPalet,
            @RequestParam(value = "referenciaProveedor", required = false) String referenciaProveedor,
            @RequestParam(value = "ean", required = false) String ean,
            @RequestParam(value = "ean_caja", required = false) String ean_caja,
            @RequestParam(value = "ean_pack", required = false) String ean_pack,
            @RequestParam(value = "largo_caja", required = false) Integer largo_caja,
            @RequestParam(value = "ancho_caja", required = false) Integer ancho_caja,
            @RequestParam(value = "alto_caja", required = false) Integer alto_caja,
            @RequestParam(value = "peso_caja", required = false) BigDecimal peso_caja,
            @RequestParam(value = "largo_unidad", required = false) Integer largo_unidad,
            @RequestParam(value = "ancho_unidad", required = false) Integer ancho_unidad,
            @RequestParam(value = "alto_unidad", required = false) Integer alto_unidad,
            @RequestParam(value = "peso_neto_unidad", required = false) BigDecimal peso_neto_unidad,
            @RequestParam(value = "peso_escurrido_unidad", required = false) BigDecimal peso_escurrido_unidad,
            @RequestParam(value = "diasCaducidad", required = false) Integer diasCaducidad,
            @RequestParam(value = "iva", required = false) String iva,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "imagen", required = false) String imagen,
            @RequestParam(value = "partidaArancelaria", required = false) String partidaArancelaria,
            @RequestParam(value = "paisOrigen", required = false) String paisOrigen,
            @RequestParam(value = "imagenUrls", required = false) List<String> imagenUrls,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestPart(value = "documentos", required = false) List<MultipartFile> documentos,
            @RequestParam(value = "leadtime", required = false) Integer leadtime,
            @RequestParam(value = "moq", required = false) Integer moq,
            @RequestParam(value = "multiplo_de_pedido", required = false) String multiploDePedido,
            @RequestParam(name = "tiposDocumentos", required = false) List<String> tiposDocumentos,
            @RequestParam(name = "documentosExistentesTipos", required = false) String documentosExistentesTipos)
            throws IOException {

        ProductoEntity producto = new ProductoEntity();
        producto.setId(id);
        producto.setDescripcion(descripcion);
        producto.setMarca(marca);
        //producto.setUnidadDeMedida(unidadDeMedida);
        //producto.setCentralizado(centralizado);
        producto.setUnidadDeCaja(unidadDeCaja);
        // producto.setUnidadDeServicio(unidadDeServicio);
        producto.setUnidadDePack(unidadDePack);
        producto.setCajasCapa(cajasCapa);
        producto.setCajasPalet(cajasPalet);
        producto.setReferenciaProveedor(referenciaProveedor);
        producto.setEan(ean);
        producto.setEan_caja(ean_caja);
        producto.setEan_pack(ean_pack);
        producto.setLargo_caja(largo_caja);
        producto.setAncho_caja(ancho_caja);
        producto.setAlto_caja(alto_caja);
        producto.setPeso_caja(peso_caja);
        producto.setLargo_unidad(largo_unidad);
        producto.setAncho_unidad(ancho_unidad);
        producto.setAlto_unidad(alto_unidad);
        producto.setPeso_neto_unidad(peso_neto_unidad);
        producto.setPeso_escurrido_unidad(peso_escurrido_unidad);
        producto.setDiasCaducidad(diasCaducidad);
        producto.setIva(iva);
        producto.setObservaciones(observaciones);
        producto.setImagen(imagen);
        producto.setPartidaArancelaria(partidaArancelaria);
        producto.setPaisOrigen(paisOrigen);
        producto.setLeadtime(leadtime);
        producto.setMoq(moq);
        producto.setMultiploDePedido(multiploDePedido);

        // Actualiza campos básicos e imágenes
        ProductoEntity updated = oProductoService.update(producto, imagenes, imagenUrls);

        // Carga el producto actualizado con relaciones
        ProductoEntity productoCompleto = oProductoService.findById(id);

        // Guarda documentos nuevos con tipo
        oProductoService.guardarDocumentosDelProducto(productoCompleto, documentos, tiposDocumentos);

        // Actualiza tipo de documentos existentes
        if (documentosExistentesTipos != null && productoCompleto.getDocumentos() != null) {
            String[] pares = documentosExistentesTipos.split(",");
            for (String tipoYId : pares) {
                String[] partes = tipoYId.split("=");
                if (partes.length == 2) {
                    try {
                        Long docId = Long.parseLong(partes[0]);
                        String nuevoTipo = partes[1];
                        productoCompleto.getDocumentos().stream()
                                .filter(doc -> doc.getId().equals(docId))
                                .findFirst()
                                .ifPresent(doc -> {
                                    doc.setTipo(nuevoTipo);
                                    try {
                                        oProductoService.guardarDocumentoExistente(doc.getId(), nuevoTipo);
                                    } catch (IOException e) {
                                        System.err
                                                .println("Error al guardar documento existente con ID: " + doc.getId());
                                        e.printStackTrace();
                                    }
                                });
                    } catch (NumberFormatException e) {
                        System.err.println("Formato incorrecto en documentosExistentesTipos: " + tipoYId);
                    }
                }
            }
        }

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocumentos(
            @PathVariable Long id,
            @RequestPart("documentos") List<MultipartFile> documentos,
            @RequestPart("tiposDocumentos") List<String> tiposDocumentos) {
        try {
            ProductoEntity producto = oProductoService.findById(id);
            oProductoService.guardarDocumentosDelProducto(producto, documentos, tiposDocumentos);
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
            oProductoService.enviarProducto(id);
            return ResponseEntity.ok("Producto enviado correctamente"); // Devolver texto plano
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el producto: " + e.getMessage());
        }
    }

}
