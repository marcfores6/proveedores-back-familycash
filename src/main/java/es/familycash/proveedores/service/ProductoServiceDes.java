package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoDocumentoEntityDes;
import es.familycash.proveedores.entity.ProductoEntityDes;
import es.familycash.proveedores.entity.ProductoImagenEntity;
import es.familycash.proveedores.entity.ProductoImagenEntityDes;
import es.familycash.proveedores.helper.ImagePathResolver;
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

        // Procesar imágenes por archivo
        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    ImagePathResolver.ImagePath pathInfo = ImagePathResolver.generate("producto", guardado.getId(),
                            file.getOriginalFilename());
                    Files.write(pathInfo.absolutePath, file.getBytes());

                    ProductoImagenEntityDes imagenEntity = new ProductoImagenEntityDes();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(pathInfo.relativeUrl);
                    oProductoImagenRepositoryDes.save(imagenEntity);

                }
            }
        }

        // Procesar imágenes por URL
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

        // Copiar valores del producto
        oProductoEntityDesFromDatabase.setDescripcion(producto.getDescripcion());
        oProductoEntityDesFromDatabase.setMarca(producto.getMarca());
        // oProductoEntityDesFromDatabase.setUnidadDeMedida(producto.getUnidadDeMedida());
        // oProductoEntityDesFromDatabase.setCentralizado(producto.getCentralizado());
        oProductoEntityDesFromDatabase.setUnidadDeCaja(producto.getUnidadDeCaja());
        // oProductoEntityDesFromDatabase.setUnidadDeServicio(producto.getUnidadDeServicio());
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

        // Procesar imágenes por archivo
        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    ImagePathResolver.ImagePath pathInfo = ImagePathResolver.generate("producto", oProductoEntityDesFromDatabase.getId(), file.getOriginalFilename());
                    Files.write(pathInfo.absolutePath, file.getBytes());

                    ProductoImagenEntityDes imagenEntityDes = new ProductoImagenEntityDes();
                    imagenEntityDes.setProducto(oProductoEntityDesFromDatabase);
                    imagenEntityDes.setImagenUrl(pathInfo.relativeUrl);
                    oProductoImagenRepositoryDes.save(imagenEntityDes);

                }
            }
        }

        // Procesar imágenes por URL
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

        // Solo eliminamos el archivo si la imagen está en el sistema de archivos (no es
        // URL externa)
        if (imagenUrl != null && imagenUrl.startsWith("/images/")) {
            String baseFolder = "C:\\imagenes-familycash";
            Path rutaCompleta = Paths.get(baseFolder, imagenUrl.replaceFirst("/", "").replace("/", "\\"));

            try {
                boolean deleted = Files.deleteIfExists(rutaCompleta);
                if (deleted) {
                    System.out.println("🗑️ Imagen eliminada físicamente: " + rutaCompleta);
                } else {
                    System.out.println("⚠️ No se encontró la imagen para eliminar: " + rutaCompleta);
                }
            } catch (IOException e) {
                System.err.println("❌ Error eliminando la imagen del disco: " + e.getMessage());
            }
        } else {
            System.out.println("🌐 Imagen externa no eliminada físicamente: " + imagenUrl);
        }

        // Finalmente, eliminamos la entrada de la base de datos
        oProductoImagenRepositoryDes.delete(imagen);
    }

    public Page<ProductoEntityDes> getPageByProveedor(Pageable pageable, String proveedorId) {
        String proveedorIdFormatted = String.format("%05d", Integer.parseInt(proveedorId));
        return oProductoRepositoryDes.findByProveedor(proveedorIdFormatted, pageable);
    }

    public void guardarDocumentosDelProducto(ProductoEntityDes producto, List<MultipartFile> documentos,
            List<String> tiposDocumentos)
            throws IOException {
        if (documentos == null || documentos.isEmpty()) {
            return;
        }

        // Ruta base donde se guardan los documentos
        String baseFolder = "./proveedores/imagenes-familycash/images/docs/producto/" + producto.getId() + "/";
        Files.createDirectories(Paths.get(baseFolder));

        List<ProductoDocumentoEntityDes> documentosParaGuardar = new ArrayList<>();

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
            String codProveedor = producto.getProveedor() != null ? producto.getProveedor()
                    : "SINPROV";
            String ean = producto.getEan() != null ? producto.getEan() : "SINEAN";
            String extension = FilenameUtils.getExtension(documento.getOriginalFilename());
            String nuevoNombre = codProveedor + "_" + ean + "_" + tipo + "." + extension;

            String filePath = baseFolder + nuevoNombre;
            Path path = Paths.get(filePath);
            Files.write(path, documento.getBytes());

            // Crear la entidad y asignar sus campos
            ProductoDocumentoEntityDes documentoEntity = new ProductoDocumentoEntityDes();
            documentoEntity.setProducto(producto);
            documentoEntity.setDocumentoUrl("/docs/producto/" + producto.getId() + "/" + nuevoNombre);
            documentoEntity.setNombreOriginal(documento.getOriginalFilename());
            documentoEntity.setTipo(tipo);

            documentosParaGuardar.add(documentoEntity);
        }

        // Guardado masivo en base de datos
        oProductoDocumentoRepositoryDes.saveAll(documentosParaGuardar);
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

        // Ruta física actual del archivo
        String baseDir = "./proveedores/imagenes-familycash/images";
        Path rutaActual = Paths.get(baseDir + doc.getDocumentoUrl());

        // Nueva ruta
        Path nuevaRuta = rutaActual.resolveSibling(nuevoNombre);

        // Renombrar físicamente el archivo si existe
        if (Files.exists(rutaActual)) {
            Files.move(rutaActual, nuevaRuta, StandardCopyOption.REPLACE_EXISTING);
        }

        // Actualizar campos en la entidad
        doc.setTipo(nuevoTipo);
        doc.setDocumentoUrl("/docs/producto/" + producto.getId() + "/" + nuevoNombre);
        doc.setNombreOriginal(nuevoNombre);

        oProductoDocumentoRepositoryDes.save(doc);
    }

    public List<ProductoDocumentoEntityDes> obtenerDocumentosDeProducto(Long productoId) {
        return oProductoDocumentoRepositoryDes.findByProductoId(productoId);
    }

    public void eliminarDocumento(Long documentoId) throws IOException {
        ProductoDocumentoEntityDes documento = oProductoDocumentoRepositoryDes.findById(documentoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        // Eliminar el archivo físico
        String filePath = "src/main/resources/static" + documento.getDocumentoUrl();
        Files.deleteIfExists(Paths.get(filePath));

        // Eliminar la referencia de la base de datos
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
