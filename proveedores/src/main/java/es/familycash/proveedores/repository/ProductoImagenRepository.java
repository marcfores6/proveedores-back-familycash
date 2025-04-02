package es.familycash.proveedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import es.familycash.proveedores.entity.ProductoImagenEntity;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagenEntity, Long>{
    
}
