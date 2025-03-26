package es.familycash.proveedores.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.familycash.proveedores.entity.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long>{

    Page<ProveedorEntity> findByEmpresaContainingOrEmailContainingOrPasswordContaining(
        String filter1, String filter2, String filter3, Pageable pageable
    );

    Optional<ProveedorEntity> findByEmail(String email);

    @Query("SELECT p FROM ProveedorEntity p JOIN FETCH p.tipoproveedor WHERE p.email = :email")
    Optional<ProveedorEntity> findByEmailWithTipo(@Param("email") String email);


    Optional<ProveedorEntity> findByEmailAndPassword(String email, String password);
    Page<ProveedorEntity> findByEmpresaContainingOrEmailContaining(
        String filter2, String filter3, Pageable oPageable);
    
}
