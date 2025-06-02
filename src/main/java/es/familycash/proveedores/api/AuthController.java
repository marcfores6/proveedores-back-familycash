package es.familycash.proveedores.api;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.familycash.proveedores.bean.ErrorBean;
import es.familycash.proveedores.bean.LoginDataBean;
import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.repository.ProveedorRepository;
import es.familycash.proveedores.service.AuthService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService oAuthService;

    @Autowired
    ProveedorRepository proveedorRepository;

   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDataBean oLogindataBean) {
    if (oAuthService.checkLogin(oLogindataBean)) {
        String token = oAuthService.getToken(oLogindataBean.getNif(), oLogindataBean.getProveedorId());
        return ResponseEntity.ok(token);
    } else {
        ErrorBean error = new ErrorBean(
            401,
            "NIF, proveedor o contraseña incorrectos",
            LocalDateTime.now()
        );
        return ResponseEntity.status(401).body(error);
    }
}


    @GetMapping("/proveedores-por-nif")
    public ResponseEntity<List<ProveedorEntity>> getProveedoresPorNif(@RequestParam String nif) {
        List<ProveedorEntity> proveedores = proveedorRepository.findAllByNif(nif);
        return ResponseEntity.ok(proveedores);
    }


    

}
