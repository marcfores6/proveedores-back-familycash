package es.familycash.proveedores.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.familycash.proveedores.entity.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar proveedores por nombre o NIF
    
    Page<ProveedorEntity> findByDescripcionContaining(String filter, Pageable pageable);

    Optional<ProveedorEntity> findByNif(String nif);

    Optional<ProveedorEntity> findByNifAndPassword(String nif, String password);
    Page<ProveedorEntity> findByNif(String filter, Pageable pageable);
    
}
