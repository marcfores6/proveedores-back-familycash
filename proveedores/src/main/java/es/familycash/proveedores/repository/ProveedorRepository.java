package es.familycash.proveedores.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long>{

    Page<ProveedorEntity> findByEmpresaContainingOrEmailContainingOrPasswordContaining(
        String filter1, String filter2, String filter3, Pageable pageable
    );

    Optional<ProveedorEntity> findByEmail(String email);

    Optional<ProveedorEntity> findByEmailAndPassword(String email, String password);
    Page<ProveedorEntity> findByEmpresaContainingOrEmailContaining(
        String filter2, String filter3, Pageable oPageable);
    
}
