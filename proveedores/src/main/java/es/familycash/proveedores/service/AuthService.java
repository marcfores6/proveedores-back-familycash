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
        if (oProveedorRepository.findByNifAndPassword(oLogindataBean.getNif(), oLogindataBean.getPassword())
                .isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, String> getClaims(String nif) {
        Map<String, String> claims = new HashMap<>();
        claims.put("nif", nif);
        return claims;
    };

    public String getToken(String nif) {
        return JWTHelper.generateToken(getClaims(nif));
    }

    public ProveedorEntity getProveedorFromToken() {
        if (oHttpServletRequest.getAttribute("nif") == null) {
            throw new UnauthorizedAccessException("No hay proveedor en la sesión");
        } else {
            String nif = oHttpServletRequest.getAttribute("nif").toString();
            return oProveedorRepository.findByNif(nif).get();
        }                
    }

    public boolean isSessionActive() {
        return oHttpServletRequest.getAttribute("nif") != null;
    }

    /*
    public boolean isAdmin() {
        return this.getProveedorFromToken().getTipoproveedor().getId() == 1L;
    }

    public boolean isClient() {
        return this.getProveedorFromToken().getTipoproveedor().getId() == 2L;
    }

    public boolean isAdminOrClient() {
        return this.isAdmin() || this.isClient();
    }

    public boolean isClientWithItsOwnData(Long id) {
        ProveedorEntity oProveedorEntity = this.getProveedorFromToken();
        return this.isClient() && oProveedorEntity.getId() == id;
    }*/

}
