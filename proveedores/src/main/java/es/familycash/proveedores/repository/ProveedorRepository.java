package es.familycash.proveedores.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.familycash.proveedores.entity.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar proveedores por nombre o NIF

    Page<ProveedorEntity> findByDescripcionContaining(String filter, Pageable pageable);

    Optional<ProveedorEntity> findByNif(String nif);

    Optional<ProveedorEntity> findByNifAndPassword(String nif, String password);

    Page<ProveedorEntity> findByNif(String filter, Pageable pageable);

    List<ProveedorEntity> findAllByNif(String nif);

    Optional<ProveedorEntity> findByIdAndNif(Long id, String nif);

    Optional<ProveedorEntity> findByTokenRecuperacion(String token);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProveedorEntity p SET p.tokenRecuperacion = :token, p.tokenExpiracion = :expiracion WHERE p.id = :id")
    int actualizarTokenRecuperacion(
            @org.springframework.data.repository.query.Param("token") String token,
            @org.springframework.data.repository.query.Param("expiracion") LocalDateTime expiracion,
            @org.springframework.data.repository.query.Param("id") Long id);


    @Query(value = "SELECT * FROM #{#entityName}", nativeQuery = true)
    List<ProveedorEntity> findAllDynamic(); // Esto no sirve con sufijos diferentes, pero te doy uno dinámico abajo


}
