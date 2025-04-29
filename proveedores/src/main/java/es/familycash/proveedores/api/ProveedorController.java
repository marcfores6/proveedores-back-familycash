package es.familycash.proveedores.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.familycash.proveedores.bean.RecuperarPasswordRequest;
import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.repository.ProveedorRepository;
import es.familycash.proveedores.service.EmailService;
import es.familycash.proveedores.service.ProveedorService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorService oProveedorService;

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    EmailService oEmailService;

    @GetMapping("")
    public ResponseEntity<Page<ProveedorEntity>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oProveedorService.getPage(oPageable, filter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> get(@PathVariable Long id) {
        return new ResponseEntity<>(oProveedorService.get(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProveedorEntity>> getAll() {
        List<ProveedorEntity> lista = oProveedorRepository.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(oProveedorService.count(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return new ResponseEntity<>(oProveedorService.delete(id), HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Long> deleteAll() {
        return new ResponseEntity<>(oProveedorService.deleteAll(), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ProveedorEntity> update(ProveedorEntity oProveedor) {
        return new ResponseEntity<>(oProveedorService.update(oProveedor), HttpStatus.OK);
    }

    @GetMapping("/bynif/{nif}")
    public ResponseEntity<ProveedorEntity> getByNif(@PathVariable String nif) {
        return new ResponseEntity<>(oProveedorService.getByNif(nif), HttpStatus.OK);
    }

    @GetMapping("/bytoken")
    public ResponseEntity<ProveedorEntity> getProveedorFromToken() {
        return new ResponseEntity<>(oProveedorService.getProveedorFromToken(), HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String newPassword) {
        ProveedorEntity proveedor = oProveedorService.getProveedorFromToken();
        oProveedorService.updatePassword(proveedor.getId(), newPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @PostMapping("/recuperar-password")
public ResponseEntity<?> recuperarPassword(@RequestBody RecuperarPasswordRequest data) {
    Optional<ProveedorEntity> optionalProveedor = oProveedorRepository.findById(data.getProveedorId());

    if (optionalProveedor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado");
    }

    ProveedorEntity proveedor = optionalProveedor.get();

    if (!proveedor.getNif().trim().equalsIgnoreCase(data.getNif().trim())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El NIF no coincide con el proveedor");
    }

    if (proveedor.getEmail() == null || proveedor.getEmail().trim().isEmpty()) {
        // Enviar respuesta para que el cliente pueda añadir el email
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este proveedor no tiene email registrado. Por favor, proporcione uno.");
    }

    // Generar token
    String token = UUID.randomUUID().toString();
    LocalDateTime expiracion = LocalDateTime.now().plusMinutes(30);

    // 🔥 FORZAR GUARDADO
    int filasActualizadas = oProveedorRepository.actualizarTokenRecuperacion(
            token,
            expiracion,
            proveedor.getId());
    System.out.println("TOKEN ACTUALIZADO: " + token + " - Filas afectadas: " + filasActualizadas);

    try {
        // Enviar correo
        oEmailService.sendRecuperacionEmail(proveedor.getEmail(), token);
    } catch (Exception e) {
        e.printStackTrace(); // 👈 Imprime el error en consola
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error enviando el correo: " + e.getMessage());
    }

    return ResponseEntity.ok(Map.of("mensaje", "Se ha enviado un correo de recuperación a " + proveedor.getEmail()));
}


    @PostMapping("/restablecer-password")
public ResponseEntity<?> restablecerPassword(@RequestParam String token, @RequestParam String newPassword, @RequestParam(required = false) String email) {
    Optional<ProveedorEntity> optionalProveedor = oProveedorRepository.findByTokenRecuperacion(token);

    if (optionalProveedor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token no válido");
    }

    ProveedorEntity proveedor = optionalProveedor.get();

    if (proveedor.getTokenExpiracion() == null || proveedor.getTokenExpiracion().isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
    }

    proveedor.setPassword(newPassword);

    // Si el proveedor no tiene email, lo actualizamos
    if (email != null && !email.isEmpty()) {
        proveedor.setEmail(email);
    }

    proveedor.setTokenRecuperacion(null); // invalidar token
    proveedor.setTokenExpiracion(null);
    oProveedorRepository.save(proveedor);

    return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
}



}
