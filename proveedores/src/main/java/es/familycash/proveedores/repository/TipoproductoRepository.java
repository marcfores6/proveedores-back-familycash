package es.familycash.proveedores.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.TipoproductoEntity;

public interface TipoproductoRepository extends JpaRepository<TipoproductoEntity, Long> {
    
    Page<TipoproductoEntity> findByDescripcionContaining(String filter, Pageable pageable);
    
}
