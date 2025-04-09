package es.familycash.proveedores.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    Page<ProductoEntity> findByDescripcionContaining(String filter, Pageable pageable);

    Page<ProductoEntity> findByProveedor(String proveedorId, Pageable pageable);

    @EntityGraph(attributePaths = { "documentos", "imagenes" })
    Optional<ProductoEntity> findById(Long id);

}
