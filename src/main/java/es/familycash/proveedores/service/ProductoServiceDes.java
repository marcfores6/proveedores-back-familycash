package es.familycash.proveedores.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoDocumentoEntityDes;
import es.familycash.proveedores.entity.ProductoEntityDes;
import es.familycash.proveedores.entity.ProductoImagenEntityDes;
import es.familycash.proveedores.helper.FtpUploader;
import es.familycash.proveedores.repository.ProductoDocumentoRepositoryDes;
import es.familycash.proveedores.repository.ProductoImagenRepositoryDes;

import es.familycash.proveedores.repository.ProductoRepositoryDes;
import es.familycash.proveedores.repository.ProveedorRepositoryDes;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoServiceDes {

    @Autowired
    ProductoDocumentoRepositoryDes oProductoDocumentoRepositoryDes;

    @Autowired
    ProductoRepositoryDes oProductoRepositoryDes;

    @Autowired
    ProductoImagenRepositoryDes oProductoImagenRepositoryDes;

    @Autowired
    ProveedorRepositoryDes oProveedorRepositoryDes;

    @Autowired
    private FtpUploader ftpUploader;

    public Page<ProductoEntityDes> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oProductoRepositoryDes.findByDescripcionContaining(filter.get(), oPageable);
        } else {
            return oProductoRepositoryDes.findAll(oPageable);
        }
    }

    public ProductoEntityDes get(Long id) {
        return oProductoRepositoryDes.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    public Long count() {
        return oProductoRepositoryDes.count();
    }

    public Long delete(Long id) {
        oProductoRepositoryDes.deleteById(id);
        return 1L;
    }

    public ProductoEntityDes create(ProductoEntityDes producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        ProductoEntityDes guardado = oProductoRepositoryDes.save(producto);

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String nombreArchivo = "producto_" + UUID.randomUUID() + "." + extension;
                    String entornoFolder = "dev";
                    String carpetaRelativa = "images/" + entornoFolder + "/producto/" + guardado.getId();

                    String url = ftpUploader.subirArchivo(file, nombreArchivo, carpetaRelativa);

                    ProductoImagenEntityDes imagenEntity = new ProductoImagenEntityDes();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(url);
                    oProductoImagenRepositoryDes.save(imagenEntity);
                }
            }
        }

        if (imagenUrls != null) {
            for (String url : imagenUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductoImagenEntityDes imagenEntity = new ProductoImagenEntityDes();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(url.trim());
                    oProductoImagenRepositoryDes.save(imagenEntity);
                }
            }
        }

        return oProductoRepositoryDes.findById(guardado.getId()).orElseThrow();
    }

    public ProductoEntityDes update(ProductoEntityDes producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        ProductoEntityDes oProductoEntityDesFromDatabase = oProductoRepositoryDes.findById(producto.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        oProductoEntityDesFromDatabase.setDescripcion(producto.getDescripcion());
        oProductoEntityDesFromDatabase.setMarca(producto.getMarca());
        oProductoEntityDesFromDatabase.setUnidadDeCaja(producto.getUnidadDeCaja());
        oProductoEntityDesFromDatabase.setUnidadDePack(producto.getUnidadDePack());
        oProductoEntityDesFromDatabase.setCajasCapa(producto.getCajasCapa());
        oProductoEntityDesFromDatabase.setCajasPalet(producto.getCajasPalet());
        oProductoEntityDesFromDatabase.setReferenciaProveedor(producto.getReferenciaProveedor());
        oProductoEntityDesFromDatabase.setEan(producto.getEan());
        oProductoEntityDesFromDatabase.setEan_caja(producto.getEan_caja());
        oProductoEntityDesFromDatabase.setEan_pack(producto.getEan_pack());
        oProductoEntityDesFromDatabase.setLargo_caja(producto.getLargo_caja());
        oProductoEntityDesFromDatabase.setAncho_caja(producto.getAncho_caja());
        oProductoEntityDesFromDatabase.setAlto_caja(producto.getAlto_caja());
        oProductoEntityDesFromDatabase.setPeso_caja(producto.getPeso_caja());
        oProductoEntityDesFromDatabase.setLargo_unidad(producto.getLargo_unidad());
        oProductoEntityDesFromDatabase.setAncho_unidad(producto.getAncho_unidad());
        oProductoEntityDesFromDatabase.setAlto_unidad(producto.getAlto_unidad());
        oProductoEntityDesFromDatabase.setPeso_neto_unidad(producto.getPeso_neto_unidad());
        oProductoEntityDesFromDatabase.setPeso_escurrido_unidad(producto.getPeso_escurrido_unidad());
        oProductoEntityDesFromDatabase.setDiasCaducidad(producto.getDiasCaducidad());
        oProductoEntityDesFromDatabase.setIva(producto.getIva());
        oProductoEntityDesFromDatabase.setObservaciones(producto.getObservaciones());
        oProductoEntityDesFromDatabase.setImagen(producto.getImagen());
        oProductoEntityDesFromDatabase.setPartidaArancelaria(producto.getPartidaArancelaria());
        oProductoEntityDesFromDatabase.setPaisOrigen(producto.getPaisOrigen());
        oProductoEntityDesFromDatabase.setEstado(producto.getEstado());
        oProductoEntityDesFromDatabase.setLeadtime(producto.getLeadtime());
        oProductoEntityDesFromDatabase.setMoq(producto.getMoq());
        oProductoEntityDesFromDatabase.setMultiploDePedido(producto.getMultiploDePedido());

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String nombreArchivo = "producto_" + UUID.randomUUID() + "." + extension;
                    String entornoFolder = "dev";
                    String carpetaRelativa = "images/" + entornoFolder + "/producto/" + producto.getId();

                    String url = ftpUploader.subirArchivo(file, nombreArchivo, carpetaRelativa);

                    ProductoImagenEntityDes imagenEntity = new ProductoImagenEntityDes();
                    imagenEntity.setProducto(oProductoEntityDesFromDatabase);
                    imagenEntity.setImagenUrl(url);
                    oProductoImagenRepositoryDes.save(imagenEntity);
                }
            }
        }

        if (imagenUrls != null) {
            for (String url : imagenUrls) {
                if (url != null && !url.isBlank()) {
                    ProductoImagenEntityDes imagenEntity = new ProductoImagenEntityDes();
                    imagenEntity.setProducto(oProductoEntityDesFromDatabase);
                    imagenEntity.setImagenUrl(url.trim());
                    oProductoImagenRepositoryDes.save(imagenEntity);
                }
            }
        }

        oProductoRepositoryDes.save(oProductoEntityDesFromDatabase);

        return oProductoRepositoryDes.findById(producto.getId()).orElseThrow();
    }

    public Long deleteAll() {
        oProductoRepositoryDes.deleteAll();
        return this.count();
    }

    public ProductoEntityDes findById(Long id) {
        return oProductoRepositoryDes.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void deleteImagen(Long id) {
        ProductoImagenEntityDes imagen = oProductoImagenRepositoryDes.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id));

        String imagenUrl = imagen.getImagenUrl();

        // Solo eliminamos del FTP si no es una URL externa
        if (imagenUrl != null && imagenUrl.startsWith("https://proveedores.familycash.es/assets/")) {
            String rutaRelativa = imagenUrl.replace("https://proveedores.familycash.es/assets/", "");
            String rutaFtpCompleta = "/www/assets/" + rutaRelativa;

            try {
                ftpUploader.eliminarArchivo(rutaFtpCompleta);
                System.out.println("🗑️ Imagen eliminada del FTP: " + rutaFtpCompleta);
            } catch (IOException e) {
                System.err.println("❌ Error eliminando la imagen del FTP: " + e.getMessage());
            }
        } else {
            System.out.println("🌐 Imagen externa no eliminada físicamente: " + imagenUrl);
        }

        oProductoImagenRepositoryDes.delete(imagen);
    }

    public Page<ProductoEntityDes> getPageByProveedor(Pageable pageable, String proveedorId) {
        String proveedorIdFormatted = String.format("%05d", Integer.parseInt(proveedorId));
        return oProductoRepositoryDes.findByProveedor(proveedorIdFormatted, pageable);
    }

    public void guardarDocumentosDelProducto(ProductoEntityDes producto, List<MultipartFile> documentos,
            List<String> tiposDocumentos) throws IOException {

        if (documentos == null || documentos.isEmpty()) {
            return; // No hay documentos, salimos sin hacer nada
        }

        for (int i = 0; i < documentos.size(); i++) {
            MultipartFile documento = documentos.get(i);
            String tipo;
            if (tiposDocumentos != null && tiposDocumentos.size() > i && tiposDocumentos.get(i) != null
                    && !tiposDocumentos.get(i).trim().isEmpty()) {
                tipo = tiposDocumentos.get(i).trim();
            } else {
                throw new IllegalArgumentException("Falta el tipo para el documento " + (i + 1));
            }

            // Generar nombre del archivo con el formato CODPROVEEDOR_EAN_T.pdf
            String codProveedor = producto.getProveedor() != null ? producto.getProveedor() : "SINPROV";
            String ean = producto.getEan() != null ? producto.getEan() : "SINEAN";
            String extension = FilenameUtils.getExtension(documento.getOriginalFilename());
            String nuevoNombre = codProveedor + "_" + ean + "_" + tipo + "." + extension;

            // Subir al FTP
            String entornoFolder = "dev";
            String subcarpetaRelativa = "docs/" + entornoFolder + "/producto/" + producto.getId();
            String urlFtp = ftpUploader.subirArchivo(documento, nuevoNombre, subcarpetaRelativa);

            // Crear y guardar el documento
            ProductoDocumentoEntityDes documentoEntityDes = new ProductoDocumentoEntityDes();
            documentoEntityDes.setProducto(producto);
            documentoEntityDes.setDocumentoUrl(urlFtp);
            documentoEntityDes.setNombreOriginal(documento.getOriginalFilename());
            documentoEntityDes.setTipo(tipo);

            oProductoDocumentoRepositoryDes.save(documentoEntityDes); // 👈 Aquí está la solución
        }
    }

    public void guardarDocumentoExistente(Long idDocumento, String nuevoTipo) throws IOException {
        ProductoDocumentoEntityDes doc = oProductoDocumentoRepositoryDes.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        ProductoEntityDes producto = doc.getProducto();
        if (producto == null) {
            throw new RuntimeException("Producto asociado no encontrado");
        }

        String codProveedor = producto.getProveedor() != null ? producto.getProveedor() : "UNKNOWN";
        String ean = producto.getEan() != null ? producto.getEan() : "0000000000000";
        String nuevoNombre = codProveedor + "_" + ean + "_" + nuevoTipo + ".pdf";

        // Extraer ruta base de la URL actual del FTP
        String entornoFolder = "dev";
        String carpetaBase = "docs/" + entornoFolder + "/producto/" + producto.getId();
        String nuevaUrl = "https://proveedores.familycash.es/assets/" + carpetaBase + "/" + nuevoNombre;

        // (Opcional) si quisieras re-subir el archivo con el nuevo nombre, deberías
        // descargarlo y volverlo a subir
        // pero eso requiere más lógica y no lo hacemos si solo renombramos la URL

        // Actualizar campos en la entidad
        doc.setTipo(nuevoTipo);
        doc.setDocumentoUrl(nuevaUrl);
        doc.setNombreOriginal(nuevoNombre);

        oProductoDocumentoRepositoryDes.save(doc);
    }

    public List<ProductoDocumentoEntityDes> obtenerDocumentosDeProducto(Long productoId) {
        return oProductoDocumentoRepositoryDes.findByProductoId(productoId);
    }

    public void eliminarDocumento(Long documentoId) {
        ProductoDocumentoEntityDes documento = oProductoDocumentoRepositoryDes.findById(documentoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        String urlPublica = documento.getDocumentoUrl(); // https://proveedores.familycash.es/assets/...
        String rutaRelativa = urlPublica.replace("https://proveedores.familycash.es/assets/", "");
        String rutaFtpCompleta = "/www/assets/" + rutaRelativa;

        try {
            ftpUploader.eliminarArchivo(rutaFtpCompleta);
            System.out.println("🗑️ Documento eliminado del FTP: " + rutaFtpCompleta);
        } catch (IOException e) {
            System.err.println("❌ Error al eliminar documento del FTP: " + e.getMessage());
            // Puedes dejarlo así o lanzar una excepción controlada si quieres avisar
            // throw new RuntimeException("No se pudo eliminar el archivo físico");
        }

        oProductoDocumentoRepositoryDes.delete(documento);
    }

    public ProductoEntityDes enviarProducto(Long id) throws Exception {
        ProductoEntityDes producto = oProductoRepositoryDes.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        producto.setEstado("ENVIADO");
        return oProductoRepositoryDes.save(producto);

    }

    public void deleteProductoYArchivos(Long id) throws IOException {
        ProductoEntityDes producto = oProductoRepositoryDes.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Eliminar imágenes físicas
        for (ProductoImagenEntityDes imagen : producto.getImagenes()) {
            deleteImagen(imagen.getId());
        }

        // Eliminar documentos físicos
        for (ProductoDocumentoEntityDes documento : producto.getDocumentos()) {
            eliminarDocumento(documento.getId());
        }

        // Eliminar el producto
        oProductoRepositoryDes.delete(producto);

        System.out.println("Producto eliminado completamente (dev): ID " + id);
    }

}
