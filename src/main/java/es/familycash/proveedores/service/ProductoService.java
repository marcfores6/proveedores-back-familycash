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

import es.familycash.proveedores.entity.ProductoDocumentoEntity;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.ProductoImagenEntity;
import es.familycash.proveedores.helper.FtpUploader;
import es.familycash.proveedores.repository.ProductoDocumentoRepository;
import es.familycash.proveedores.repository.ProductoImagenRepository;

import es.familycash.proveedores.repository.ProductoRepository;
import es.familycash.proveedores.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoService {

    @Autowired
    ProductoDocumentoRepository oProductoDocumentoRepository;

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    ProductoImagenRepository oProductoImagenRepository;

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    private FtpUploader ftpUploader;


    public Page<ProductoEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oProductoRepository.findByDescripcionContaining(filter.get(), oPageable);
        } else {
            return oProductoRepository.findAll(oPageable);
        }
    }

    public ProductoEntity get(Long id) {
        return oProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    public Long count() {
        return oProductoRepository.count();
    }

    public Long delete(Long id) {
        oProductoRepository.deleteById(id);
        return 1L;
    }

    public ProductoEntity create(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        ProductoEntity guardado = oProductoRepository.save(producto);

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String nombreArchivo = "producto_" + UUID.randomUUID() + "." + extension;
                    String entornoFolder = "prod";
                    String carpetaRelativa = "images/" + entornoFolder + "/producto/" + guardado.getId();

                    String url = ftpUploader.subirArchivo(file, nombreArchivo, carpetaRelativa);

                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(url);
                    oProductoImagenRepository.save(imagenEntity);
                }
            }
        }

        if (imagenUrls != null) {
            for (String url : imagenUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(url.trim());
                    oProductoImagenRepository.save(imagenEntity);
                }
            }
        }

        return oProductoRepository.findById(guardado.getId()).orElseThrow();
    }

    public ProductoEntity update(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        ProductoEntity oProductoEntityFromDatabase = oProductoRepository.findById(producto.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        oProductoEntityFromDatabase.setDescripcion(producto.getDescripcion());
        oProductoEntityFromDatabase.setMarca(producto.getMarca());
        oProductoEntityFromDatabase.setUnidadDeCaja(producto.getUnidadDeCaja());
        oProductoEntityFromDatabase.setUnidadDePack(producto.getUnidadDePack());
        oProductoEntityFromDatabase.setCajasCapa(producto.getCajasCapa());
        oProductoEntityFromDatabase.setCajasPalet(producto.getCajasPalet());
        oProductoEntityFromDatabase.setReferenciaProveedor(producto.getReferenciaProveedor());
        oProductoEntityFromDatabase.setEan(producto.getEan());
        oProductoEntityFromDatabase.setEan_caja(producto.getEan_caja());
        oProductoEntityFromDatabase.setEan_pack(producto.getEan_pack());
        oProductoEntityFromDatabase.setLargo_caja(producto.getLargo_caja());
        oProductoEntityFromDatabase.setAncho_caja(producto.getAncho_caja());
        oProductoEntityFromDatabase.setAlto_caja(producto.getAlto_caja());
        oProductoEntityFromDatabase.setPeso_caja(producto.getPeso_caja());
        oProductoEntityFromDatabase.setLargo_unidad(producto.getLargo_unidad());
        oProductoEntityFromDatabase.setAncho_unidad(producto.getAncho_unidad());
        oProductoEntityFromDatabase.setAlto_unidad(producto.getAlto_unidad());
        oProductoEntityFromDatabase.setPeso_neto_unidad(producto.getPeso_neto_unidad());
        oProductoEntityFromDatabase.setPeso_escurrido_unidad(producto.getPeso_escurrido_unidad());
        oProductoEntityFromDatabase.setDiasCaducidad(producto.getDiasCaducidad());
        oProductoEntityFromDatabase.setIva(producto.getIva());
        oProductoEntityFromDatabase.setObservaciones(producto.getObservaciones());
        oProductoEntityFromDatabase.setImagen(producto.getImagen());
        oProductoEntityFromDatabase.setPartidaArancelaria(producto.getPartidaArancelaria());
        oProductoEntityFromDatabase.setPaisOrigen(producto.getPaisOrigen());
        oProductoEntityFromDatabase.setEstado(producto.getEstado());
        oProductoEntityFromDatabase.setLeadtime(producto.getLeadtime());
        oProductoEntityFromDatabase.setMoq(producto.getMoq());
        oProductoEntityFromDatabase.setMultiploDePedido(producto.getMultiploDePedido());

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String nombreArchivo = "producto_" + UUID.randomUUID() + "." + extension;
                    String entornoFolder = "prod";
                    String carpetaRelativa = "images/" + entornoFolder + "/producto/" + producto.getId();

                    String url = ftpUploader.subirArchivo(file, nombreArchivo, carpetaRelativa);

                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(oProductoEntityFromDatabase);
                    imagenEntity.setImagenUrl(url);
                    oProductoImagenRepository.save(imagenEntity);
                }
            }
        }

        if (imagenUrls != null) {
            for (String url : imagenUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(oProductoEntityFromDatabase);
                    imagenEntity.setImagenUrl(url.trim());
                    oProductoImagenRepository.save(imagenEntity);
                }
            }
        }

        oProductoRepository.save(oProductoEntityFromDatabase);
        return oProductoRepository.findById(producto.getId()).orElseThrow();
    }

    public Long deleteAll() {
        oProductoRepository.deleteAll();
        return this.count();
    }

    public ProductoEntity findById(Long id) {
        return oProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void deleteImagen(Long id) {
        ProductoImagenEntity imagen = oProductoImagenRepository.findById(id)
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

        oProductoImagenRepository.delete(imagen);
    }

    public Page<ProductoEntity> getPageByProveedor(Pageable pageable, String proveedorId) {
        String proveedorIdFormatted = String.format("%05d", Integer.parseInt(proveedorId));
        return oProductoRepository.findByProveedor(proveedorIdFormatted, pageable);
    }

    public void guardarDocumentosDelProducto(ProductoEntity producto, List<MultipartFile> documentos,
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
            String entornoFolder = "prod";
            String subcarpetaRelativa = "docs/" + entornoFolder + "/producto/" + producto.getId();

            String urlFtp = ftpUploader.subirArchivo(documento, nuevoNombre, subcarpetaRelativa);

            // Crear y guardar el documento
            ProductoDocumentoEntity documentoEntity = new ProductoDocumentoEntity();
            documentoEntity.setProducto(producto);
            documentoEntity.setDocumentoUrl(urlFtp);
            documentoEntity.setNombreOriginal(documento.getOriginalFilename());
            documentoEntity.setTipo(tipo);

            oProductoDocumentoRepository.save(documentoEntity); // 👈 Aquí está la solución
        }
    }

    public void guardarDocumentoExistente(Long idDocumento, String nuevoTipo) throws IOException {
        ProductoDocumentoEntity doc = oProductoDocumentoRepository.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        ProductoEntity producto = doc.getProducto();
        if (producto == null) {
            throw new RuntimeException("Producto asociado no encontrado");
        }

        String codProveedor = producto.getProveedor() != null ? producto.getProveedor() : "UNKNOWN";
        String ean = producto.getEan() != null ? producto.getEan() : "0000000000000";
        String nuevoNombre = codProveedor + "_" + ean + "_" + nuevoTipo + ".pdf";

        // Extraer ruta base de la URL actual del FTP
        String entornoFolder = "prod";
        String carpetaBase = "docs/" + entornoFolder + "/producto/" + producto.getId();
        String nuevaUrl = "https://proveedores.familycash.es/assets/" + carpetaBase + "/" + nuevoNombre;

        // (Opcional) si quisieras re-subir el archivo con el nuevo nombre, deberías
        // descargarlo y volverlo a subir
        // pero eso requiere más lógica y no lo hacemos si solo renombramos la URL

        // Actualizar campos en la entidad
        doc.setTipo(nuevoTipo);
        doc.setDocumentoUrl(nuevaUrl);
        doc.setNombreOriginal(nuevoNombre);

        oProductoDocumentoRepository.save(doc);
    }

    public List<ProductoDocumentoEntity> obtenerDocumentosDeProducto(Long productoId) {
        return oProductoDocumentoRepository.findByProductoId(productoId);
    }

    public void eliminarDocumento(Long documentoId) {
        ProductoDocumentoEntity documento = oProductoDocumentoRepository.findById(documentoId)
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

        oProductoDocumentoRepository.delete(documento);
    }

    public ProductoEntity enviarProducto(Long id) throws Exception {
        ProductoEntity producto = oProductoRepository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        producto.setEstado("ENVIADO");
        return oProductoRepository.save(producto);
    }

    public void deleteProductoYArchivos(Long id) throws IOException {
        ProductoEntity producto = oProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Eliminar imágenes físicas
        for (ProductoImagenEntity imagen : producto.getImagenes()) {
            deleteImagen(imagen.getId());
        }

        // Eliminar documentos físicos
        for (ProductoDocumentoEntity documento : producto.getDocumentos()) {
            eliminarDocumento(documento.getId());
        }

        // Eliminar el producto
        oProductoRepository.delete(producto);

        System.out.println("Producto eliminado completamente (prod): ID " + id);
    }

}
