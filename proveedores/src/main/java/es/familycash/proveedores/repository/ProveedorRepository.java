package es.familycash.proveedores.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long>{

    Page<ProveedorEntity> findByEmpresaContainingOrEmailContaining(
        String filter1, String filter2, Pageable pageable
    );
    
}
