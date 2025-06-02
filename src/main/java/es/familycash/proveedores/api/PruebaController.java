package es.familycash.proveedores.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PruebaController {

    @GetMapping("/hola")
    public String hola() {
        return "¡Hola desde el backend real de FamilyCash!";
    }
}