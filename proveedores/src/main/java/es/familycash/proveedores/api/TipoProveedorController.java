package es.familycash.proveedores.api;

import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/tipoproveedor")
public class TipoProveedorController {

    @Autowired TipoProveedorService oTipoProveedorService;

    @GetMapping("")
    public ResponseEntity<Page<TipoProveedorEntity>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<Page><TipoproveedorEntity>>(oTipoProveedorService.getPage(oPageable, filter), HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoProveedorEntity> getTipoProveedorById (@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(oTipoProveedorService.get(id));
    }
}
