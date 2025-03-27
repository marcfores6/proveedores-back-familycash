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

import es.familycash.proveedores.entity.TipoproductoEntity;
import es.familycash.proveedores.service.TipoproductoService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/tipoproducto")
public class TipoProductoController {
    
    @Autowired
    TipoproductoService oTipoProductoService;

    @GetMapping("")
    public ResponseEntity<Page<TipoproductoEntity>> getPage(
     Pageable oPageable,
     @RequestParam Optional<String> filter) {
        return new ResponseEntity<Page<TipoproductoEntity>>(oTipoProductoService.getPage(oPageable, filter), HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoproductoEntity> getTipoProductoById (@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(oTipoProductoService.get(id));
    }
}

