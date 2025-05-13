package es.familycash.proveedores.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.familycash.proveedores.config.AppConfig;

@RestController
@RequestMapping("/entorno")
@CrossOrigin(origins = "*")
public class EntornoController {

    @Autowired
    AppConfig appConfig;

    @GetMapping
    public ResponseEntity<?> getEntorno() {
        return ResponseEntity.ok(Map.of("desarrollo", appConfig.isDesarrollo()));
    }

    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarEntorno(@RequestParam boolean desarrollo) {
        appConfig.setEntornoTemporal(desarrollo);
        return ResponseEntity.ok(Map.of("mensaje", "Entorno cambiado temporalmente", "desarrollo", desarrollo));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetearEntorno() {
        appConfig.resetEntorno();
        return ResponseEntity.ok(Map.of("mensaje", "Entorno restablecido al valor de application.properties"));
    }
}
