package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.ProductoImagenEntity;
import es.familycash.proveedores.helper.ImagePathResolver;
import es.familycash.proveedores.repository.ProductoImagenRepository;

import es.familycash.proveedores.repository.ProductoRepository;
import es.familycash.proveedores.repository.ProveedorRepository;

@Service
public class ProductoService {

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
        oProductoEntityFromDatabase.setDescripcionTic(producto.getDescripcionTic());
        oProductoEntityFromDatabase.setDepartamento(producto.getDepartamento());
        oProductoEntityFromDatabase.setFamilia(producto.getFamilia());
        oProductoEntityFromDatabase.setSubfamilia(producto.getSubfamilia());
        oProductoEntityFromDatabase.setMarca(producto.getMarca());
        oProductoEntityFromDatabase.setUnidadDeMedida(producto.getUnidadDeMedida());
        oProductoEntityFromDatabase.setCantidad(producto.getCantidad());
        oProductoEntityFromDatabase.setCentralizado(producto.getCentralizado());
        oProductoEntityFromDatabase.setAra_cen(producto.getAra_cen());
        oProductoEntityFromDatabase.setApeso(producto.getApeso());
        oProductoEntityFromDatabase.setUnidadDeCaja(producto.getUnidadDeCaja());
        oProductoEntityFromDatabase.setUnidadDeServicio(producto.getUnidadDeServicio());
        oProductoEntityFromDatabase.setPk(producto.getPk());
        oProductoEntityFromDatabase.setCajasCapa(producto.getCajasCapa());
        oProductoEntityFromDatabase.setCajasPalet(producto.getCajasPalet());
        oProductoEntityFromDatabase.setProveedor(producto.getProveedor());
        oProductoEntityFromDatabase.setReferenciaProveedor(producto.getReferenciaProveedor());
        oProductoEntityFromDatabase.setEan(producto.getEan());
        oProductoEntityFromDatabase.setEan_c(producto.getEan_c());
        oProductoEntityFromDatabase.setEan_p(producto.getEan_p());
        oProductoEntityFromDatabase.setLargo(producto.getLargo());
        oProductoEntityFromDatabase.setAncho(producto.getAncho());
        oProductoEntityFromDatabase.setAlto(producto.getAlto());
        oProductoEntityFromDatabase.setPeso(producto.getPeso());
        oProductoEntityFromDatabase.setDiasCaducidad(producto.getDiasCaducidad());
        oProductoEntityFromDatabase.setAra_cen(producto.getAra_cen());
        oProductoEntityFromDatabase.setIva(producto.getIva());
        oProductoEntityFromDatabase.setPrecioVenta(producto.getPrecioVenta());
        oProductoEntityFromDatabase.setPvp_hom(producto.getPvp_hom());
        oProductoEntityFromDatabase.setPvp_and(producto.getPvp_and());
        oProductoEntityFromDatabase.setPvp_cat(producto.getPvp_cat());
        oProductoEntityFromDatabase.setPrecioTarifa(producto.getPrecioTarifa());
        oProductoEntityFromDatabase.setPro_fac(producto.getPro_fac());
        oProductoEntityFromDatabase.setPrecioNeto(producto.getPrecioNeto());
        oProductoEntityFromDatabase.setPro_ffac(producto.getPro_ffac());
        oProductoEntityFromDatabase.setPro_neton(producto.getPro_neton());
        oProductoEntityFromDatabase.setArt_mkd(producto.getArt_mkd());
        oProductoEntityFromDatabase.setArticuloSustituido(producto.getArticuloSustituido());
        oProductoEntityFromDatabase.setInsertedBy(producto.getInsertedBy());
        oProductoEntityFromDatabase.setInsertedAt(producto.getInsertedAt());
        oProductoEntityFromDatabase.setUpdateBy(producto.getUpdateBy());
        oProductoEntityFromDatabase.setUpdateAt(producto.getUpdateAt());
        oProductoEntityFromDatabase.setStatus(producto.getStatus());
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
    
    
    
    

}
