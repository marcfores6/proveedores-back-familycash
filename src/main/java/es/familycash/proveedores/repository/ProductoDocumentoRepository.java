package es.familycash.proveedores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.familycash.proveedores.entity.ProductoDocumentoEntity;

@Repository
public interface ProductoDocumentoRepository extends JpaRepository<ProductoDocumentoEntity, Long> {
    List<ProductoDocumentoEntity> findByProductoId(Long productoId);
}
