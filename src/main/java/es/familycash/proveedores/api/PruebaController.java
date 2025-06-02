package es.familycash.proveedores.api;

@RestController
@RequestMapping("/api")
public class PruebaController {

    @GetMapping("/hola")
    public String hola() {
        return "¡Hola desde el backend real de FamilyCash!";
    }
}