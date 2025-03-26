package es.familycash.proveedores.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.repository.TipoproveedorRepository;

@Service
public class TipoProveedor {

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
