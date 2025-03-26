package es.familycash.proveedores.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.service.ProveedorService;

import java.util.Optional;

import org.springframework.data.domain.Page;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorService oProveedorService;

    @GetMapping("")
    public ResponseEntity<Page<ProveedorEntity>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oProveedorService.getPage(oPageable, filter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> get(@PathVariable Long id) {
        return new ResponseEntity<>(oProveedorService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(oProveedorService.count(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return new ResponseEntity<>(oProveedorService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> create(
            @RequestParam("Empresa") String empresa,
            @RequestParam("Email") String email,
            @RequestParam("Password") String password,
            @RequestParam(value = "Imagen", required = false) MultipartFile imagen

    ) {
        return oProveedorService.createProveedor(empresa, email, password, imagen);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("Empresa") String empresa,
            @RequestParam("Email") String email,
            @RequestParam("Password") String password,
            @RequestParam(value = "Imagen", required = false) MultipartFile imagen) {
        return oProveedorService.updateProveedor(id, empresa, email, password, imagen);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Long> deleteAll() {
        return new ResponseEntity<Long>(oProveedorService.deleteAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        // Buscar el Proveedor directamente, ya que findById lanza la excepción si no lo
        // encuentra
        ProveedorEntity oProveedor = oProveedorService.findById(id);

        // Retornar la imagen como respuesta HTTP
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(oProveedor.getImagen());
    }

    @GetMapping("/byemail/{email}")
    public ResponseEntity<ProveedorEntity> getProveedorByEmail(@PathVariable(value = "email") String email) {
        return ResponseEntity.ok(oProveedorService.getByEmail(email));
    }

}
