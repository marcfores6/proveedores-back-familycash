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

    public boolean checkLogin(LoginDataBean loginData) {
        return oProveedorRepository.findById(loginData.getProveedorId())
        .filter(p -> p.getNif().equals(loginData.getNif()))
        .filter(p -> p.getPassword().equals(loginData.getPassword()))
        .isPresent();
    }

    private Map<String, String> getClaims(ProveedorEntity proveedor) {
        Map<String, String> claims = new HashMap<>();
        claims.put("nif", proveedor.getNif());
        claims.put("proveedorId", proveedor.getId().toString());
        return claims;
    }

    public String getToken(String nif, Long proveedorId) {
        ProveedorEntity proveedor = oProveedorRepository.findById(proveedorId)
            .filter(p -> p.getNif().equals(nif))
            .orElseThrow();
        return JWTHelper.generateToken(getClaims(proveedor));
    }
    
    public ProveedorEntity getProveedorFromToken() {
        Object nifAttr = oHttpServletRequest.getAttribute("nif");
        if (nifAttr == null) throw new UnauthorizedAccessException("No hay proveedor en la sesión");
        String nif = nifAttr.toString();
        return oProveedorRepository.findByNif(nif).orElseThrow();
    }

    public ProveedorEntity getProveedorFromTokenId() {
        Object proveedorIdAttr = oHttpServletRequest.getAttribute("proveedorId");
        if (proveedorIdAttr == null) throw new UnauthorizedAccessException("No hay proveedor en la sesión");
        Long proveedorId = Long.parseLong(proveedorIdAttr.toString());
        return oProveedorRepository.findById(proveedorId).orElseThrow();
    }
    

    public boolean isSessionActive() {
        return oHttpServletRequest.getAttribute("nif") != null;
    }
}