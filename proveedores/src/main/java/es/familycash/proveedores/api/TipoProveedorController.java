package es.familycash.proveedores.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.familycash.proveedores.entity.TipoproveedorEntity;
import es.familycash.proveedores.service.TipoproveedorService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/tipoproveedor")
public class TipoProveedorController {

    @Autowired
    TipoproveedorService oTipoProveedorService;

    @GetMapping("")
    public ResponseEntity<Page<TipoproveedorEntity>> getPage(
     Pageable oPageable,
     @RequestParam Optional<String> filter) {
        return new ResponseEntity<Page<TipoproveedorEntity>>(oTipoProveedorService.getPage(oPageable, filter), HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoproveedorEntity> getTipoProveedorById (@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(oTipoProveedorService.get(id));
    }
}
