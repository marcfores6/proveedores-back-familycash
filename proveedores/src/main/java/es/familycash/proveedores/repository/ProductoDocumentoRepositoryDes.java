package es.familycash.proveedores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.familycash.proveedores.entity.ProductoDocumentoEntityDes;

@Repository
public interface ProductoDocumentoRepositoryDes extends JpaRepository<ProductoDocumentoEntityDes, Long> {
    List<ProductoDocumentoEntityDes> findByProductoId(Long productoId);
}
