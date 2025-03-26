package es.familycash.proveedores.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.TipoproveedorEntity;

public interface TipoproveedorRepository extends JpaRepository<TipoproveedorEntity, Long> {
    
    Page<TipoproveedorEntity> findByDescripcionContaining(String filter, Pageable pageable);
}
