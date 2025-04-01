package es.familycash.proveedores.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import es.familycash.proveedores.entity.ProductoEntity;
import es.familycash.proveedores.entity.TipoproductoEntity;
import es.familycash.proveedores.service.ProductoService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoService oProductoService;

    @GetMapping("")
    public ResponseEntity<Page<ProductoEntity>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oProductoService.getPage(oPageable, filter), HttpStatus.OK);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoEntity> get(@PathVariable Long codigo) {
        return new ResponseEntity<>(oProductoService.get(codigo), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(oProductoService.count(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{codigo}")
    public ResponseEntity<Long> delete(@PathVariable Long codigo) {
        return new ResponseEntity<>(oProductoService.delete(codigo), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> create(
            @RequestParam("Nombre") String nombre,
            @RequestParam("TipoProducto") TipoproductoEntity tipoProducto,
            @RequestParam(value = "Imagen", required = false) List<MultipartFile> imagen,
            @RequestParam(value = "ImagenUrl", required = false) String imagenUrl) {
        return oProductoService.createProducto(nombre, tipoProducto, imagen, imagenUrl);
    }

    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable Long codigo,
            @RequestParam("Nombre") String nombre,
            @RequestParam("TipoProducto") TipoproductoEntity tipoProducto,
            @RequestParam(value = "Imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "ImagenUrl", required = false) String imagenUrl) {
        return oProductoService.updateProducto(codigo, nombre, tipoProducto, imagen, imagenUrl);
    }

    @DeleteMapping("/imagen/{id}")
    public ResponseEntity<?> deleteImagen(@PathVariable Long id) {
        oProductoService.deleteImagen(id);
        return ResponseEntity.ok().build();
    }

}
