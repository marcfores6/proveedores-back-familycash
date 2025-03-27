package es.familycash.proveedores.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.entity.TipoproductoEntity;
import es.familycash.proveedores.repository.TipoproductoRepository;



@Service
public class TipoproductoService {

    @Autowired
    private TipoproductoRepository oTipoproductoRepository;

    public TipoproductoEntity get(Long id){
        Optional <TipoproductoEntity> tipoproducto = oTipoproductoRepository.findById(id);
        if (tipoproducto.isPresent()){
            return tipoproducto.get();
        } else {
            throw new RuntimeException("Tipo de producto no encontrado con ID: " + id);
        }
    }

    public Page<TipoproductoEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oTipoproductoRepository.findByDescripcionContaining(filter.get(), oPageable);
        } else {
            return oTipoproductoRepository.findAll(oPageable);
        }
    }
    
}

