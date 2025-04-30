package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoDocumentoEntity;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.ProductoImagenEntity;
import es.familycash.proveedores.helper.ImagePathResolver;
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

        // Procesar imágenes por archivo
        if (imagenes != null) {
            StringBuilder imagenesGuardadas = new StringBuilder();
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    ImagePathResolver.ImagePath ruta = ImagePathResolver.generate("producto", guardado.getId(),
                            file.getOriginalFilename());
                    Files.createDirectories(ruta.absolutePath.getParent());
                    Files.write(ruta.absolutePath, file.getBytes()); // Aquí se puede lanzar IOException

                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));
                    oProductoImagenRepository.save(imagenEntity);

                    // Concatenar la URL de la imagen al campo ara_image
                    if (imagenesGuardadas.length() > 0) {
                        imagenesGuardadas.append(",");
                    }
                    imagenesGuardadas.append("/").append(ruta.relativeUrl.replace("\\", "/"));
                }
            }
            // Guardar las URLs de las imágenes en ara_image
            producto.setImagen(imagenesGuardadas.toString());
        }

        // Procesar imágenes por URL
        if (imagenUrls != null) {
            StringBuilder imagenesGuardadas = new StringBuilder();
            for (String url : imagenUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(guardado);
                    imagenEntity.setImagenUrl(url.trim());
                    oProductoImagenRepository.save(imagenEntity);

                    // Concatenar la URL al campo ara_image
                    if (imagenesGuardadas.length() > 0) {
                        imagenesGuardadas.append(",");
                    }
                    imagenesGuardadas.append(url.trim());
                }
            }
            // Guardar las URLs de las imágenes en ara_image
            producto.setImagen(imagenesGuardadas.toString());
        }

        return oProductoRepository.save(producto);
    }

    public ProductoEntity update(ProductoEntity producto, List<MultipartFile> imagenes, List<String> imagenUrls)
            throws IOException {
        ProductoEntity oProductoEntityFromDatabase = oProductoRepository.findById(producto.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        // Copiar valores del producto
        oProductoEntityFromDatabase.setDescripcion(producto.getDescripcion());
        oProductoEntityFromDatabase.setMarca(producto.getMarca());
        oProductoEntityFromDatabase.setUnidadDeMedida(producto.getUnidadDeMedida());
        oProductoEntityFromDatabase.setCentralizado(producto.getCentralizado());
        oProductoEntityFromDatabase.setUnidadDeCaja(producto.getUnidadDeCaja());
        oProductoEntityFromDatabase.setUnidadDeServicio(producto.getUnidadDeServicio());
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
        oProductoEntityFromDatabase.setDiasCaducidad(producto.getDiasCaducidad());
        oProductoEntityFromDatabase.setIva(producto.getIva());
        oProductoEntityFromDatabase.setObservaciones(producto.getObservaciones());
        oProductoEntityFromDatabase.setImagen(producto.getImagen());
        oProductoEntityFromDatabase.setPartidaArancelaria(producto.getPartidaArancelaria());
        oProductoEntityFromDatabase.setPaisOrigen(producto.getPaisOrigen());

        // Procesar imágenes por archivo
        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                            "producto", oProductoEntityFromDatabase.getId(), file.getOriginalFilename());

                    Files.createDirectories(ruta.absolutePath.getParent());
                    Files.write(ruta.absolutePath, file.getBytes());

                    ProductoImagenEntity imagenEntity = new ProductoImagenEntity();
                    imagenEntity.setProducto(oProductoEntityFromDatabase);
                    imagenEntity.setImagenUrl("/images/producto/" + oProductoEntityFromDatabase.getId() + "/"
                            + file.getOriginalFilename());
                    oProductoImagenRepository.save(imagenEntity);
                }
            }
        }

        // Procesar imágenes por URL
        if (imagenUrls != null) {
            for (String url : imagenUrls) {
                if (url != null && !url.isBlank()) {
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

    public void guardarDocumentosDelProducto(ProductoEntity producto, List<MultipartFile> documentos)
            throws IOException {
        if (documentos == null || documentos.isEmpty()) {
            return;
        }

        // 🧩 Ruta base que quieres, respetando tu configuración Spring para los
        // estáticos
        String baseFolder = "./proveedores/imagenes-familycash/images/docs/producto/" + producto.getId() + "/";

        // Crear el directorio si no existe
        Files.createDirectories(Paths.get(baseFolder));

        // Lista de entidades para guardar en lote
        List<ProductoDocumentoEntity> documentosParaGuardar = new ArrayList<>();

        for (MultipartFile documento : documentos) {
            String originalFilename = documento.getOriginalFilename();
            String filePath = baseFolder + originalFilename;

            // Guardar el archivo en el sistema de archivos
            Path path = Paths.get(filePath);
            Files.write(path, documento.getBytes());

            // Crear entidad y añadir a la lista para guardar en la base de datos
            ProductoDocumentoEntity documentoEntity = new ProductoDocumentoEntity();
            documentoEntity.setProducto(producto);
            documentoEntity.setDocumentoUrl("/docs/producto/" + producto.getId() + "/" + originalFilename); // Relativa
                                                                                                            // para el
                                                                                                            // frontend
            documentoEntity.setNombreOriginal(originalFilename);

            documentosParaGuardar.add(documentoEntity);
        }

        // Guardar todos los documentos de una sola vez en la base de datos
        oProductoDocumentoRepository.saveAll(documentosParaGuardar);
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

}
