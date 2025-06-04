package es.familycash.proveedores.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoDocumentoEntity;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.ProductoImagenEntity;
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

    private final String FTP_HOST = "proveedores.familycash.es";
    private final int FTP_PORT = 21;
    private final String FTP_USER = "sistemas@grupofamily.es"; // Cambiar
    private final String FTP_PASS = "DinahostingSistemas32$"; // Cambiar
    private final String FTP_RUTA_BASE = "/www/assets/";

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

   public ProductoEntity create(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls) throws IOException {
        ProductoEntity guardado = oProductoRepository.save(producto);

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String nombreArchivo = "producto_" + UUID.randomUUID() + "." + extension;
                    String carpeta = String.valueOf(guardado.getId());
                    String url = subirPorFTP(file.getInputStream(), carpeta, nombreArchivo);

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

    public ProductoEntity update(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls) throws IOException {
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
                    String carpeta = String.valueOf(producto.getId());
                    String url = subirPorFTP(file.getInputStream(), carpeta, nombreArchivo);

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

    private String subirPorFTP(InputStream inputStream, String subcarpeta, String nombreArchivo) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(FTP_HOST, FTP_PORT);
            ftp.login(FTP_USER, FTP_PASS);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String rutaFinal = FTP_RUTA_BASE + subcarpeta + "/";
            String[] partes = rutaFinal.split("/");
            String pathAcumulado = "";
            for (String parte : partes) {
                if (!parte.isBlank()) {
                    pathAcumulado += "/" + parte;
                    ftp.makeDirectory(pathAcumulado);
                    ftp.changeWorkingDirectory(pathAcumulado);
                }
            }

            boolean subido = ftp.storeFile(nombreArchivo, inputStream);
            if (!subido) {
                throw new IOException("No se pudo subir el archivo al servidor FTP.");
            }

            return "https://proveedores.familycash.es/assets/images/producto/" + subcarpeta + "/" + nombreArchivo;
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
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
        oProductoImagenRepository.delete(imagen);
    }

    public Page<ProductoEntity> getPageByProveedor(Pageable pageable, String proveedorId) {
        String proveedorIdFormatted = String.format("%05d", Integer.parseInt(proveedorId));
        return oProductoRepository.findByProveedor(proveedorIdFormatted, pageable);
    }

    public void guardarDocumentosDelProducto(ProductoEntity producto, List<MultipartFile> documentos,
            List<String> tiposDocumentos)
            throws IOException {
        if (documentos == null || documentos.isEmpty()) {
            return;
        }

        // Ruta base donde se guardan los documentos
        String baseFolder = "./proveedores/imagenes-familycash/images/docs/producto/" + producto.getId() + "/";
        Files.createDirectories(Paths.get(baseFolder));

        List<ProductoDocumentoEntity> documentosParaGuardar = new ArrayList<>();

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
            ProductoDocumentoEntity documentoEntity = new ProductoDocumentoEntity();
            documentoEntity.setProducto(producto);
            documentoEntity.setDocumentoUrl("/docs/producto/" + producto.getId() + "/" + nuevoNombre);
            documentoEntity.setNombreOriginal(documento.getOriginalFilename());
            documentoEntity.setTipo(tipo);

            documentosParaGuardar.add(documentoEntity);
        }

        // Guardado masivo en base de datos
        oProductoDocumentoRepository.saveAll(documentosParaGuardar);
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

        oProductoDocumentoRepository.save(doc);
    }

    public List<ProductoDocumentoEntity> obtenerDocumentosDeProducto(Long productoId) {
        return oProductoDocumentoRepository.findByProductoId(productoId);
    }

    public void eliminarDocumento(Long documentoId) throws IOException {
        ProductoDocumentoEntity documento = oProductoDocumentoRepository.findById(documentoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        // Eliminar el archivo físico
        String filePath = "src/main/resources/static" + documento.getDocumentoUrl();
        Files.deleteIfExists(Paths.get(filePath));

        // Eliminar la referencia de la base de datos
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
