package es.familycash.proveedores.service;

import es.familycash.proveedores.config.RequestContext;
import es.familycash.proveedores.entity.*;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceRouter {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoServiceDes productoServiceDes;

    @Autowired
    private RequestContext requestContext;

    private boolean isDev() {
        return requestContext.isDev();
    }

    public ProductoEntity create(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        if (isDev()) {
            ProductoEntityDes creado = productoServiceDes.create(
                    toProductoEntityDes(producto), imagenes, imagenUrls);
            return toProductoEntity(creado);
            
        } else {
            return productoService.create(producto, imagenes, imagenUrls);
        }
    }

    public ProductoEntity update(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        if (isDev()) {
            ProductoEntityDes actualizado = productoServiceDes.update(
                    toProductoEntityDes(producto), imagenes, imagenUrls);
            return toProductoEntity(actualizado);
        } else {
            return productoService.update(producto, imagenes, imagenUrls);
        }
    }

    public ProductoEntity findById(Long id) {
        if (isDev()) {
            return toProductoEntity(productoServiceDes.findById(id));
        } else {
            return productoService.findById(id);
        }
    }

    public ProductoEntity get(Long id) {
        if (isDev()) {
            ProductoEntityDes entityDes = productoServiceDes.findById(id);
            if (entityDes == null) {
                throw new RuntimeException("Producto no encontrado con id: " + id);
            }
            return toProductoEntity(entityDes);
        } else {
            ProductoEntity entity = productoService.findById(id);
            if (entity == null) {
                throw new RuntimeException("Producto no encontrado con id: " + id);
            }
            return entity;
        }
    }

    public void guardarDocumentosDelProducto(ProductoEntity producto, List<MultipartFile> documentos,
            List<String> tiposDocumentos) throws IOException {
        if (isDev()) {
            productoServiceDes.guardarDocumentosDelProducto(
                    toProductoEntityDes(producto), documentos, tiposDocumentos);
        } else {
            productoService.guardarDocumentosDelProducto(producto, documentos, tiposDocumentos);
        }
    }

    public void guardarDocumentoExistente(Long id, String tipo) throws IOException {
        if (isDev()) {
            productoServiceDes.guardarDocumentoExistente(id, tipo);
        } else {
            productoService.guardarDocumentoExistente(id, tipo);
        }
    }

    public void deleteImagen(Long id) throws IOException {
        if (isDev()) {
            productoServiceDes.deleteImagen(id);
        } else {
            productoService.deleteImagen(id);
        }
    }

    public void deleteDocumento(Long id) {
        if (isDev()) {
            try {
                productoServiceDes.eliminarDocumento(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                productoService.eliminarDocumento(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<ProductoDocumentoEntity> obtenerDocumentosDeProducto(Long productoId) {
        if (isDev()) {
            return productoServiceDes.obtenerDocumentosDeProducto(productoId).stream()
                    .map(this::toProductoDocumentoEntity)
                    .collect(Collectors.toList());
        } else {
            return productoService.obtenerDocumentosDeProducto(productoId);
        }
    }

    public ProductoEntity enviarProducto(Long id) throws Exception {
        if (isDev()) {
            ProductoEntityDes enviado = productoServiceDes.enviarProducto(id);
            return toProductoEntity(enviado);
        } else {
            return productoService.enviarProducto(id);
        }
    }

    // ---------- Conversión mínima entre entidades ----------

    private ProductoEntity toProductoEntity(ProductoEntityDes des) {
        ProductoEntity prod = new ProductoEntity();
        prod.setId(des.getId());
        prod.setDescripcion(des.getDescripcion());
        prod.setMarca(des.getMarca());
        prod.setUnidadDeMedida(des.getUnidadDeMedida());
        prod.setCentralizado(des.getCentralizado());
        prod.setUnidadDeCaja(des.getUnidadDeCaja());
        prod.setUnidadDePack(des.getUnidadDePack());
        prod.setCajasCapa(des.getCajasCapa());
        prod.setCajasPalet(des.getCajasPalet());
        prod.setReferenciaProveedor(des.getReferenciaProveedor());
        prod.setEan(des.getEan());
        prod.setEan_caja(des.getEan_caja());
        prod.setEan_pack(des.getEan_pack());
        prod.setLargo_caja(des.getLargo_caja());
        prod.setAncho_caja(des.getAncho_caja());
        prod.setAlto_caja(des.getAlto_caja());
        prod.setPeso_caja(des.getPeso_caja());
        prod.setLargo_unidad(des.getLargo_unidad());
        prod.setAncho_unidad(des.getAncho_unidad());
        prod.setAlto_unidad(des.getAlto_unidad());
        prod.setPeso_neto_unidad(des.getPeso_neto_unidad());
        prod.setPeso_escurrido_unidad(des.getPeso_escurrido_unidad());
        prod.setDiasCaducidad(des.getDiasCaducidad());
        prod.setIva(des.getIva());
        prod.setObservaciones(des.getObservaciones());
        prod.setImagen(des.getImagen());
        prod.setPartidaArancelaria(des.getPartidaArancelaria());
        prod.setPaisOrigen(des.getPaisOrigen());
        prod.setEstado(des.getEstado());
        prod.setLeadtime(des.getLeadtime());
        prod.setMoq(des.getMoq());
        prod.setMultiploDePedido(des.getMultiploDePedido());
        prod.setProveedor(des.getProveedor());

        // 👉 Aquí mapeamos también las imágenes
        prod.setImagenes(
                des.getImagenes() != null
                        ? des.getImagenes().stream().map(img -> {
                            ProductoImagenEntity imagen = new ProductoImagenEntity();
                            imagen.setId(img.getId());
                            imagen.setImagenUrl(img.getImagenUrl());
                            return imagen;
                        }).collect(Collectors.toList())
                        : new ArrayList<>());

        // 👉 Aquí mapeamos también los documentos
        prod.setDocumentos(
                des.getDocumentos() != null
                        ? des.getDocumentos().stream().map(doc -> {
                            ProductoDocumentoEntity documento = new ProductoDocumentoEntity();
                            documento.setId(doc.getId());
                            documento.setDocumentoUrl(doc.getDocumentoUrl());
                            documento.setNombreOriginal(doc.getNombreOriginal());
                            documento.setTipo(doc.getTipo());
                            return documento;
                        }).collect(Collectors.toList())
                        : new ArrayList<>());

        return prod;
    }

    private ProductoEntityDes toProductoEntityDes(ProductoEntity prod) {
        ProductoEntityDes des = new ProductoEntityDes();
        des.setId(prod.getId());
        des.setDescripcion(prod.getDescripcion());
        des.setMarca(prod.getMarca());
        des.setUnidadDeMedida(prod.getUnidadDeMedida());
        des.setCentralizado(prod.getCentralizado());
        des.setUnidadDeCaja(prod.getUnidadDeCaja());
        des.setUnidadDePack(prod.getUnidadDePack());
        des.setCajasCapa(prod.getCajasCapa());
        des.setCajasPalet(prod.getCajasPalet());
        des.setReferenciaProveedor(prod.getReferenciaProveedor());
        des.setEan(prod.getEan());
        des.setEan_caja(prod.getEan_caja());
        des.setEan_pack(prod.getEan_pack());
        des.setLargo_caja(prod.getLargo_caja());
        des.setAncho_caja(prod.getAncho_caja());
        des.setAlto_caja(prod.getAlto_caja());
        des.setPeso_caja(prod.getPeso_caja());
        des.setLargo_unidad(prod.getLargo_unidad());
        des.setAncho_unidad(prod.getAncho_unidad());
        des.setAlto_unidad(prod.getAlto_unidad());
        des.setPeso_neto_unidad(prod.getPeso_neto_unidad());
        des.setPeso_escurrido_unidad(prod.getPeso_escurrido_unidad());
        des.setDiasCaducidad(prod.getDiasCaducidad());
        des.setIva(prod.getIva());
        des.setObservaciones(prod.getObservaciones());
        des.setImagen(prod.getImagen());
        des.setPartidaArancelaria(prod.getPartidaArancelaria());
        des.setPaisOrigen(prod.getPaisOrigen());
        des.setEstado(prod.getEstado());
        des.setLeadtime(prod.getLeadtime());
        des.setMoq(prod.getMoq());
        des.setMultiploDePedido(prod.getMultiploDePedido());
        des.setProveedor(prod.getProveedor());
        return des;
    }

    private ProductoDocumentoEntity toProductoDocumentoEntity(ProductoDocumentoEntityDes des) {
        ProductoDocumentoEntity doc = new ProductoDocumentoEntity();
        doc.setId(des.getId());
        doc.setDocumentoUrl(des.getDocumentoUrl());
        doc.setNombreOriginal(des.getNombreOriginal());
        doc.setTipo(des.getTipo());
        return doc;
    }

    public Page<ProductoEntity> getPage(Pageable pageable, Optional<String> filter) {
        if (isDev()) {
            return productoServiceDes.getPage(pageable, filter).map(this::toProductoEntity);
        } else {
            return productoService.getPage(pageable, filter);
        }
    }

    public Page<ProductoEntity> getPageByProveedor(Pageable pageable, String proveedorId) {
        if (isDev()) {
            return productoServiceDes.getPageByProveedor(pageable, proveedorId).map(this::toProductoEntity);
        } else {
            return productoService.getPageByProveedor(pageable, proveedorId);
        }
    }

    public Long count() {
        return isDev() ? productoServiceDes.count() : productoService.count();
    }

    public Long delete(Long id) {
        return isDev() ? productoServiceDes.delete(id) : productoService.delete(id);
    }

    public void eliminarDocumento(Long id) throws IOException {
        if (isDev()) {
            productoServiceDes.eliminarDocumento(id);
        } else {
            productoService.eliminarDocumento(id);
        }
    }

    public void deleteProductoYArchivos(Long id) throws IOException {
        if (isDev()) {
            productoServiceDes.deleteProductoYArchivos(id);
        } else {
            productoService.deleteProductoYArchivos(id);
        }
    }

}
