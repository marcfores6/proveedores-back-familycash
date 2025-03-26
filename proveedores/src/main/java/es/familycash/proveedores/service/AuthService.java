package es.familycash.proveedores.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.bean.LoginDataBean;
import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.exception.UnauthorizedAccessException;
import es.familycash.proveedores.repository.ProveedorRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    @Autowired
    JWTService JWTHelper;

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    HttpServletRequest oHttpServletRequest;

    public boolean checkLogin(LoginDataBean oLogindataBean) {
        if (oProveedorRepository.findByEmailAndPassword(oLogindataBean.getEmail(), oLogindataBean.getPassword())
                .isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, String> getClaims(String email) {
        Map<String, String> claims = new HashMap<>();
        claims.put("email", email);
        return claims;
    };

    public String getToken(String email) {
        return JWTHelper.generateToken(getClaims(email));
    }

    public ProveedorEntity getProveedorFromToken() {
        if (oHttpServletRequest.getAttribute("email") == null) {
            throw new UnauthorizedAccessException("No hay Proveedor en la sesión");
        } else {
            String email = oHttpServletRequest.getAttribute("email").toString();
            return oProveedorRepository.findByEmail(email).get();
        }                
    }

    public boolean isSessionActive() {
        return oHttpServletRequest.getAttribute("email") != null;
    }

    public boolean isAdmin() {
        return this.getProveedorFromToken().getTipoproveedor().getId() == 1L;
    }

    public boolean isContable() {
        return this.getProveedorFromToken().getTipoproveedor().getId() == 2L;
    }

    public boolean isAdminOrContable() {
        return this.isAdmin() || this.isContable();
    }

    public boolean isContableWithItsOwnData(Long id) {
        ProveedorEntity oProveedorEntity = this.getProveedorFromToken();
        return this.isContable() && oProveedorEntity.getId() == id;
    }

}
