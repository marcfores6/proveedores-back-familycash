package es.familycash.proveedores.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import es.familycash.proveedores.config.AppConfig;

public class ConfigController {

    @Autowired
    AppConfig appConfig;

@GetMapping("/entorno")
public ResponseEntity<?> getEntorno() {
    return ResponseEntity.ok(Map.of("desarrollo", appConfig.isDesarrollo()));
}

    
}
