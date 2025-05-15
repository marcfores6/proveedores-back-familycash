package es.familycash.proveedores.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.familycash.proveedores.entity.ProveedorEntityDes;

public interface ProveedorRepositoryDes extends JpaRepository<ProveedorEntityDes, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar proveedores por nombre o NIF

    Page<ProveedorEntityDes> findByDescripcionContaining(String filter, Pageable pageable);

    Optional<ProveedorEntityDes> findByNif(String nif);

    Optional<ProveedorEntityDes> findByNifAndPassword(String nif, String password);

    Page<ProveedorEntityDes> findByNif(String filter, Pageable pageable);

    List<ProveedorEntityDes> findAllByNif(String nif);

    Optional<ProveedorEntityDes> findByIdAndNif(Long id, String nif);

    Optional<ProveedorEntityDes> findByTokenRecuperacion(String token);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProveedorEntityDes p SET p.tokenRecuperacion = :token, p.tokenExpiracion = :expiracion WHERE p.id = :id")
    int actualizarTokenRecuperacion(
            @org.springframework.data.repository.query.Param("token") String token,
            @org.springframework.data.repository.query.Param("expiracion") LocalDateTime expiracion,
            @org.springframework.data.repository.query.Param("id") Long id);


}
