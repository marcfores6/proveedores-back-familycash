package es.familycash.proveedores.service;

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
        return oProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        // return oProductoRepository.findById(id).get();
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
        return oProductoRepository.save(oProductoEntityFromDatabase);
    }

    public Long deleteAll() {
        oProductoRepository.deleteAll();
        return this.count();
    }
    
}
