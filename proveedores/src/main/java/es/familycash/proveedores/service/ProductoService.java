package es.familycash.proveedores.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.repository.ProductoRepository;
import es.familycash.proveedores.entity.ProductoEntity;

@Service
public class ProductoService {

    @Autowired
    ProductoRepository oProductoRepository;

    public Page<ProductoEntity> getPage(Pageable oPageable, Optional<String> filter) {

        if (filter.isPresent()) {
            return oProductoRepository
                    .findByNombreContaining(
                            filter.get(),
                            oPageable);
        } else {
            return oProductoRepository.findAll(oPageable);
        }
    }

    public ProductoEntity get(Long codigo) {
        return oProductoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con Codigo: " + codigo));
    }

    //public void saveImagen(Long id, String base64Foto) {
     //   Optional<ProductoEntity> oProducto = oProductoRepository.findById(id);
       // if (oProducto.isPresent()) {
     //       ProductoEntity producto = oProducto.get();
      //      byte[] imagen = Base64.getDecoder().decode(base64Foto);
       //     producto.setImagen(imagen);
        //    oProductoRepository.save(producto);
        //}
    //}

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

    public ResponseEntity<?> createProducto(String nombre, MultipartFile imagen) {
        try {

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Todos los campos son obligatorios."));
            }

            ProductoEntity nuevoProducto = new ProductoEntity();
            nuevoProducto.setNombre(nombre);

            if (imagen != null && !imagen.isEmpty()) {
                nuevoProducto.setImagen(imagen.getBytes());
            }

            ProductoEntity productoCreado = oProductoRepository.save(nuevoProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al subir la imagen."));
        }

    }

    public ResponseEntity<?> updateProducto(Long codigo, String nombre, MultipartFile imagen) {
        try {

            ProductoEntity productoExistente = oProductoRepository.findById(codigo)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con Codigo: " + codigo));

            productoExistente.setNombre(nombre);

            if (imagen != null && !imagen.isEmpty()) {
                productoExistente.setImagen(imagen.getBytes());
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

    //public ProductoEntity update(ProductoEntity oProductoEntity) {
    //    ProductoEntity oProductoEntityFromDatabase = oProductoRepository.findById(oProductoEntity.getCodigo()).get();
    //    if (oProductoEntity.getNombre() != null) {
    //        oProductoEntityFromDatabase.setNombre(oProductoEntity.getNombre());
    //    }
    //    if (oProductoEntity.getImagen() != null) {
    //            oProductoEntityFromDatabase.setImagen(oProductoEntity.getImagen());
    //    }
    //    return oProductoRepository.save(oProductoEntityFromDatabase);
    // }

    public Long deleteAll() {
        oProductoRepository.deleteAll();
        return this.count();
    }

    public ProductoEntity findById(Long codigo) {
        return oProductoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}
