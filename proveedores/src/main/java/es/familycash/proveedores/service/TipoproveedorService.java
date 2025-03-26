package es.familycash.proveedores.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.entity.TipoproveedorEntity;
import es.familycash.proveedores.repository.TipoproveedorRepository;

@Service
public class TipoproveedorService {

    @Autowired
    private TipoproveedorRepository oTipoproveedorRepository;

    public TipoproveedorEntity get(Long id){
        Optional <TipoproveedorEntity> tipoproveedor = oTipoproveedorRepository.findById(id);
        if (tipoproveedor.isPresent()){
            return tipoproveedor.get();
        } else {
            throw new RuntimeException("Tipo de proveedor no encontrado con ID: " + id);
        }
    }

    public Page<TipoproveedorEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oTipoproveedorRepository.findByDescripcionContaining(filter.get(), oPageable);
        } else {
            return oTipoproveedorRepository.findAll(oPageable);
        }
    }
    
}
