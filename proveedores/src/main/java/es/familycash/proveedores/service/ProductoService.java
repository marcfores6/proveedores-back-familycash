package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import es.familycash.proveedores.entity.ProductoImagenEntity;
import es.familycash.proveedores.entity.TipoproductoEntity;
import es.familycash.proveedores.helper.ImagePathResolver;
import es.familycash.proveedores.repository.ProductoImagenRepository;
import es.familycash.proveedores.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    private ProductoImagenRepository oProductoImagenRepository;

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

    public ResponseEntity<?> createProducto(String nombre, TipoproductoEntity tipoProducto,
            List<MultipartFile> imagenes, String imagenUrl) {

        try {
            ProductoEntity nuevoProducto = new ProductoEntity();
            nuevoProducto.setNombre(nombre);
            nuevoProducto.setTipoproducto(tipoProducto);

            // Guardamos el producto
            ProductoEntity productoGuardado = oProductoRepository.save(nuevoProducto);

            // Si se proporcionan imágenes, las guardamos
            if (imagenes != null && !imagenes.isEmpty()) {
                for (MultipartFile imagen : imagenes) {
                    ProductoImagenEntity productoImagen = new ProductoImagenEntity();
                    productoImagen.setProducto(productoGuardado); // Relacionamos la imagen con el producto

                    // Generar la ruta usando ImagePathResolver
                    ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                            "articulo", productoGuardado.getCodigo(), imagen.getOriginalFilename());

                    // Guardar la imagen en disco
                    try {
                        ImagePathResolver.saveImage(ruta.absolutePath, imagen.getBytes());
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.singletonMap("error", "Error al guardar la imagen."));
                    }

                    // Guardamos la imagen en la base de datos
                    productoImagen.setImagen(imagen.getBytes());
                    productoImagen.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));

                    // Guardar la relación de imagen
                    oProductoImagenRepository.save(productoImagen);
                }
            } else if (imagenUrl != null && !imagenUrl.isEmpty()) {
                // Si no se subieron imágenes, pero se proporcionó una URL, la guardamos
                ProductoImagenEntity productoImagen = new ProductoImagenEntity();
                productoImagen.setProducto(productoGuardado);
                productoImagen.setImagenUrl(imagenUrl);
                oProductoImagenRepository.save(productoImagen);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al guardar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }

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

    public void deleteImagen(Long id) {
        oProductoImagenRepository.deleteById(id);
    }

}
