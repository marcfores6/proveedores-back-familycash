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
import es.familycash.proveedores.entity.ProveedorEntityDes;
import es.familycash.proveedores.service.EmailService;
import es.familycash.proveedores.service.JWTService;
import es.familycash.proveedores.service.ProveedorService;
import es.familycash.proveedores.service.ProveedorServiceRouter;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorService oProveedorService;

    @Autowired
    EmailService oEmailService;

    @Autowired
    JWTService jwtService;

    @Autowired
    private ProveedorServiceRouter proveedorService;

    @GetMapping("")
    public ResponseEntity<Page<?>> getPage(Pageable oPageable, @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(proveedorService.getPage(oPageable, filter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<?> opt = proveedorService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado");
        }
        return ResponseEntity.ok(opt.get());
    }

    @GetMapping("/all")
    public ResponseEntity<List<?>> getAll() {
        List<?> lista = proveedorService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(proveedorService.count(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return new ResponseEntity<>(proveedorService.delete(id), HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Long> deleteAll() {
        return new ResponseEntity<>(proveedorService.deleteAll(), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Object proveedor) {
        proveedorService.save(proveedor);
        return ResponseEntity.ok(proveedor);
    }

    @GetMapping("/bynif/{nif}")
    public ResponseEntity<?> getByNif(@PathVariable String nif) {
        Optional<?> opt = proveedorService.findByNif(nif);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado con ese NIF");
        }
        return ResponseEntity.ok(opt.get());
    }

    @GetMapping("/bytoken")
    public ResponseEntity<?> getProveedorFromToken(HttpServletRequest request) {
        String nif = jwtService.getNifFromRequest(request);
        Optional<?> opt = proveedorService.findByNif(nif);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado");
        }
        return ResponseEntity.ok(opt.get());

    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String newPassword, HttpServletRequest request) {
        String nif = jwtService.getNifFromRequest(request);
        proveedorService.updatePassword(nif, newPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody RecuperarPasswordRequest data) {
        Optional<?> optionalProveedor = proveedorService.findById(data.getProveedorId());

        if (optionalProveedor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado");
        }

        Object proveedorObj = optionalProveedor.get();

        String nif = null;
        String email = null;
        Long id = null;

        if (proveedorObj instanceof ProveedorEntity proveedor) {
            nif = proveedor.getNif();
            email = proveedor.getEmail();
            id = proveedor.getId();
        } else if (proveedorObj instanceof ProveedorEntityDes proveedorDes) {
            nif = proveedorDes.getNif();
            email = proveedorDes.getEmail();
            id = proveedorDes.getId();
        }

        if (!nif.trim().equalsIgnoreCase(data.getNif().trim())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El NIF no coincide con el proveedor");
        }

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Este proveedor no tiene email registrado. Por favor, proporcione uno.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(30);

        int filasActualizadas = proveedorService.actualizarTokenRecuperacion(token, expiracion, id);
        System.out.println("TOKEN ACTUALIZADO: " + token + " - Filas afectadas: " + filasActualizadas);

        try {
            oEmailService.sendRecuperacionEmail(email, token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error enviando el correo: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("mensaje", "Se ha enviado un correo de recuperación a " + email));
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<?> restablecerPassword(@RequestParam String token, @RequestParam String newPassword,
            @RequestParam(required = false) String email) {
        Optional<?> optionalProveedor = proveedorService.findByTokenRecuperacion(token);

        if (optionalProveedor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token no válido");
        }

        Object proveedorObj = optionalProveedor.get();

        if (proveedorObj instanceof ProveedorEntity proveedor) {
            if (proveedor.getTokenExpiracion() == null
                    || proveedor.getTokenExpiracion().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
            }
            proveedor.setPassword(newPassword);
            if (email != null && !email.isEmpty())
                proveedor.setEmail(email);
            proveedor.setTokenRecuperacion(null);
            proveedor.setTokenExpiracion(null);
            proveedorService.save(proveedor);
        } else if (proveedorObj instanceof ProveedorEntityDes proveedorDes) {
            if (proveedorDes.getTokenExpiracion() == null
                    || proveedorDes.getTokenExpiracion().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
            }
            proveedorDes.setPassword(newPassword);
            if (email != null && !email.isEmpty())
                proveedorDes.setEmail(email);
            proveedorDes.setTokenRecuperacion(null);
            proveedorDes.setTokenExpiracion(null);
            proveedorService.save(proveedorDes);
        }

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestParam String email, HttpServletRequest request) {
        String nif = jwtService.getNifFromRequest(request);

        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Formato de email no válido. Debe ser tipo usuario@dominio.com/.es/... ");
        }

        proveedorService.updateEmail(nif, email);
        return ResponseEntity.ok("Email actualizado correctamente");
    }

}
