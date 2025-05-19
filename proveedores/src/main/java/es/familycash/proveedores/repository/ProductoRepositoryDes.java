package es.familycash.proveedores.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProductoEntityDes;

public interface ProductoRepositoryDes extends JpaRepository<ProductoEntityDes, Long> {

    Page<ProductoEntityDes> findByDescripcionContaining(String filter, Pageable pageable);

    Page<ProductoEntityDes> findByProveedor(String proveedorId, Pageable pageable);

    @EntityGraph(attributePaths = { "documentos", "imagenes" })
    Optional<ProductoEntityDes> findById(Long id);

}
