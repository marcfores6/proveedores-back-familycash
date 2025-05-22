package es.familycash.proveedores.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.familycash.proveedores.entity.ProductoImagenEntityDes;

@Repository
public interface ProductoImagenRepositoryDes extends JpaRepository<ProductoImagenEntityDes, Long>{
    List<ProductoImagenEntityDes> findByProductoId(Long productoId);

}
