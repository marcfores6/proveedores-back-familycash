package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.ProductoImagen;
import es.familycash.proveedores.entity.TipoproductoEntity;
import es.familycash.proveedores.helper.ImagePathResolver;
import es.familycash.proveedores.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    ProductoRepository oProductoRepository;

    public Page<ProductoEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oProductoRepository.findByNombreContaining(filter.get(), oPageable);
        } else {
            return oProductoRepository.findAll(oPageable);
        }
    }

    public ProductoEntity get(Long codigo) {
        return oProductoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con Codigo: " + codigo));
    }

    public Long count() {
        return oProductoRepository.count();
    }

    public Long delete(Long id) {
        oProductoRepository.deleteById(id);
        return 1L;
    }

    public ProductoEntity create(ProductoEntity oProductoEntity) {
        return oProductoRepository.save(oProductoEntity);
    }

    public ResponseEntity<?> createProducto(String nombre, TipoproductoEntity tipoproducto,
            List<MultipartFile> imagenes, String imagenUrl) {

        try {
            if (nombre == null || nombre.trim().isEmpty() || tipoproducto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Todos los campos son obligatorios."));
            }

            ProductoEntity nuevoProducto = new ProductoEntity();
            nuevoProducto.setNombre(nombre);
            nuevoProducto.setTipoproducto(tipoproducto);

            // Guardar el producto en la base de datos
            ProductoEntity productoGuardado = oProductoRepository.save(nuevoProducto);

            // Guardar las imágenes
            if (imagenes != null && !imagenes.isEmpty()) {
                for (MultipartFile imagen : imagenes) {
                    ProductoImagen productoImagen = new ProductoImagen();
                    // Aquí estamos creando una nueva imagen para cada archivo
                    ImagePathResolver.ImagePath ruta = ImagePathResolver.generate("articulo",
                            productoGuardado.getCodigo(), imagen.getOriginalFilename());
                    Files.createDirectories(ruta.absolutePath.getParent());
                    Files.write(ruta.absolutePath, imagen.getBytes());

                    productoImagen.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));
                    productoImagen.setProducto(productoGuardado);

                    // Guardamos la imagen asociada al producto
                    productoGuardado.getImagenes().add(productoImagen);
                }
            }

            oProductoRepository.save(productoGuardado);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al guardar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }

    /*
     * public ResponseEntity<?> createProducto(String nombre, TipoproductoEntity
     * tipoproducto, MultipartFile imagen,
     * String imagenUrl) {
     * try {
     * if (nombre == null || nombre.trim().isEmpty() || tipoproducto == null) {
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
     * .body(Collections.singletonMap("error",
     * "Todos los campos son obligatorios."));
     * }
     * 
     * ProductoEntity nuevoProducto = new ProductoEntity();
     * nuevoProducto.setNombre(nombre);
     * nuevoProducto.setTipoproducto(tipoproducto);
     * 
     * ProductoEntity productoGuardado = oProductoRepository.save(nuevoProducto);
     * 
     * if (imagen != null && !imagen.isEmpty()) {
     * ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
     * "articulo", productoGuardado.getCodigo(), imagen.getOriginalFilename());
     * 
     * Files.createDirectories(ruta.absolutePath.getParent());
     * Files.write(ruta.absolutePath, imagen.getBytes());
     * 
     * productoGuardado.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));
     * } else if (imagenUrl != null && !imagenUrl.isBlank()) {
     * productoGuardado.setImagenUrl(imagenUrl);
     * }
     * 
     * ProductoEntity productoFinal = oProductoRepository.save(productoGuardado);
     * return ResponseEntity.status(HttpStatus.CREATED).body(productoFinal);
     * 
     * } catch (IOException e) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body(Collections.singletonMap("error", "Error al guardar la imagen."));
     * } catch (Exception e) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
     * }
     * }
     */
    public ResponseEntity<?> updateProducto(Long codigo, String nombre, TipoproductoEntity tipoproducto,
            MultipartFile imagen, String imagenUrl) {
        try {
            ProductoEntity productoExistente = oProductoRepository.findById(codigo)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con Codigo: " + codigo));

            productoExistente.setNombre(nombre);
            productoExistente.setTipoproducto(tipoproducto);

            if (imagen != null && !imagen.isEmpty()) {
                ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                        "articulo", codigo, imagen.getOriginalFilename());

                Files.createDirectories(ruta.absolutePath.getParent());
                Files.write(ruta.absolutePath, imagen.getBytes());

                productoExistente.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));
            } else if (imagenUrl != null && !imagenUrl.isBlank()) {
                productoExistente.setImagenUrl(imagenUrl);
            }

            ProductoEntity productoActualizado = oProductoRepository.save(productoExistente);
            return ResponseEntity.status(HttpStatus.OK).body(productoActualizado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al subir la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }

    public Long deleteAll() {
        oProductoRepository.deleteAll();
        return this.count();
    }

    public ProductoEntity findById(Long codigo) {
        return oProductoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}
