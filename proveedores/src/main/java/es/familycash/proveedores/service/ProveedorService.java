package es.familycash.proveedores.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProveedorEntity;

import es.familycash.proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService{

    @Autowired 
    ProveedorRepository oProveedorRepository;

    public Page<ProveedorEntity> getPage(Pageable oPageable, Optional<String> filter) {

        if (filter.isPresent()) {
            return oProveedorRepository
                    .findByEmpresaContainingOrEmailContainingOrPasswordContaining(
                            filter.get(), filter.get(), filter.get(),
                            oPageable);
        } else {
            return oProveedorRepository.findAll(oPageable);
        }
    }

    public ProveedorEntity get(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
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

    public ResponseEntity<?> createProveedor(String empresa, String email, String password, MultipartFile imagen) {
        try{

            if (empresa == null || empresa.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Todos los campos son obligatorios."));

        }

        ProveedorEntity nuevoProveedor = new ProveedorEntity();
        nuevoProveedor.setEmpresa(empresa);
        nuevoProveedor.setEmail(email);
        nuevoProveedor.setPassword(password);

        if (imagen != null && !imagen.isEmpty()) {
            nuevoProveedor.setImagen(imagen.getBytes());
        }

        ProveedorEntity proveedorCreado = oProveedorRepository.save(nuevoProveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al subir la imagen."));
        }
    }



public ResponseEntity<?> updateProveedor(Long id, String empresa, String email, String password, MultipartFile imagen) {
    try{

        ProveedorEntity proveedorExistente = oProveedorRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        proveedorExistente.setEmpresa(empresa);
        proveedorExistente.setEmail(email);
        proveedorExistente.setPassword(password);

        if (imagen != null && !imagen.isEmpty()) {
            proveedorExistente.setImagen(imagen.getBytes());
        }

        ProveedorEntity proveedorActualizado = oProveedorRepository.save(proveedorExistente);
        return ResponseEntity.status(HttpStatus.OK).body(proveedorActualizado);



    }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al subir la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }



    //public ProveedorEntity update(ProveedorEntity oProveedorEntity) {
    //    ProveedorEntity oProveedorEntityFromDatabase = oProveedorRepository.findById(oProveedorEntity.getId()).get();
    //    if (oProveedorEntity.getEmpresa() != null) {
    //        oProveedorEntityFromDatabase.setEmpresa(oProveedorEntity.getEmpresa());
    //        oProveedorEntityFromDatabase.setEmpresa(oProveedorEntity.getEmpresa());
    //    }
    //    if (oProveedorEntity.getEmail() != null) {
    //        oProveedorEntityFromDatabase.setEmail(oProveedorEntity.getEmail());
    //    }
    //    if (oProveedorEntity.getPassword() != null) {
    //        oProveedorEntityFromDatabase.setPassword(oProveedorEntity.getPassword());
    //    }
    //    if (oProveedorEntity.getImagen() != null) {
     //       oProveedorEntityFromDatabase.setImagen(oProveedorEntity.getImagen());
     //   }
     //   return oProveedorRepository.save(oProveedorEntityFromDatabase);
     //}
    
    public Long deleteAll() {
        oProveedorRepository.deleteAll();
        return this.count();
    }

    public ProveedorEntity findById(Long id) {
        return oProveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }
}
