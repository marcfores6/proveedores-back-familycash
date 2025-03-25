package es.familycash.proveedores.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.repository.ProveedorRepository;
import es.familycash.proveedores.exception.ResourceNotFoundException;

@Service
public class ProveedorService{

    @Autowired 
    ProveedorRepository oProveedorRepository;

    public Page<ProveedorEntity> getPage(Pageable oPageable, Optional<String> filter) {

        if (filter.isPresent()) {
            return oProveedorRepository
                    .findByEmpresaContainingOrEmailContaining(
                            filter.get(), filter.get(),
                            oPageable);
        } else {
            return oProveedorRepository.findAll(oPageable);
        }
    }

    public ProveedorEntity get(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        // return oProveedorRepository.findById(id).get();
    }
    
    public Long count() {
        return oProveedorRepository.count();
    }

    public Long delete(Long id) {
        oProveedorRepository.deleteById(id);
        return 1L;
    }

    public ProveedorEntity create(ProveedorEntity oProveedorEntity) {
        return oProveedorRepository.save(oProveedorEntity);
    }

    public ProveedorEntity update(ProveedorEntity oProveedorEntity) {
        ProveedorEntity oProveedorEntityFromDatabase = oProveedorRepository.findById(oProveedorEntity.getId()).get();
        if (oProveedorEntity.getEmpresa() != null) {
            oProveedorEntityFromDatabase.setEmpresa(oProveedorEntity.getEmpresa());
        }
        if (oProveedorEntity.getEmail() != null) {
            oProveedorEntityFromDatabase.setEmail(oProveedorEntity.getEmail());
        }
        return oProveedorRepository.save(oProveedorEntityFromDatabase);
    }
    
    public Long deleteAll() {
        oProveedorRepository.deleteAll();
        return this.count();
    }
}
