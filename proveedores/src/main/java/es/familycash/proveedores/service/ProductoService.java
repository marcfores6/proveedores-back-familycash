package es.familycash.proveedores.service;

import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.repository.ProductoRepository;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.exception.ResourceNotFoundException;

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

    public ProductoEntity get(Long id) {
        Optional <ProductoEntity> oProducto = oProductoRepository.findById(id);
        if (oProducto.isPresent()) {
            ProductoEntity producto = oProducto.get();

            if(producto.getImagen() != null) {
                String base64Foto = Base64.getEncoder().encodeToString(producto.getImagen());
            }
            return producto;
        } 
        return null;
    }

    public void saveImagen(Long id, String base64Foto){
        Optional <ProductoEntity> oProducto = oProductoRepository.findById(id);
        if (oProducto.isPresent()){
            ProductoEntity producto = oProducto.get();
            byte[] imagen = Base64.getDecoder().decode(base64Foto);
            producto.setImagen(imagen);
            oProductoRepository.save(producto);
        }
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

    public ProductoEntity update(ProductoEntity oProductoEntity) {
        ProductoEntity oProductoEntityFromDatabase = oProductoRepository.findById(oProductoEntity.getCodigo()).get();
        if (oProductoEntity.getNombre() != null) {
            oProductoEntityFromDatabase.setNombre(oProductoEntity.getNombre());
        }
        if (oProductoEntity.getImagen() != null) {
            oProductoEntityFromDatabase.setImagen(oProductoEntity.getImagen());
        }
        return oProductoRepository.save(oProductoEntityFromDatabase);
    }

    public Long deleteAll() {
        oProductoRepository.deleteAll();
        return this.count();
    }

    public ProductoEntity uploadImagen(Long codigo, byte[] imagen) {
        Optional <ProductoEntity> oProducto = oProductoRepository.findById(codigo);
        if (oProducto.isPresent()){
            ProductoEntity producto = oProducto.get();
            producto.setImagen(imagen);
            return oProductoRepository.save(producto);
        }
        return null;
    }

    public ProductoEntity findById(Long codigo) {
        return oProductoRepository.findById(codigo)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}
