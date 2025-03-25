package es.familycash.proveedores.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long>{

    Page<ProductoEntity> findByNombreContaining(String filter, Pageable pageable);
    
}
