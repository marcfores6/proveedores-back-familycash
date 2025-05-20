package es.familycash.proveedores.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.bean.LoginDataBean;
import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.entity.ProveedorEntityDes;
import es.familycash.proveedores.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    @Autowired
    JWTService JWTHelper;

    @Autowired
    ProveedorServiceRouter proveedorService;

    @Autowired
    HttpServletRequest oHttpServletRequest;

    public boolean checkLogin(LoginDataBean loginData) {
        return proveedorService.findById(loginData.getProveedorId())
                .filter(p -> {
                    if (p instanceof ProveedorEntity pe) {
                        return pe.getNif().trim().equalsIgnoreCase(loginData.getNif().trim())
                                && pe.getPassword().equals(loginData.getPassword());
                    } else if (p instanceof ProveedorEntityDes pd) {
                        return pd.getNif().trim().equalsIgnoreCase(loginData.getNif().trim())
                                && pd.getPassword().equals(loginData.getPassword());
                    }
                    return false;
                })
                .isPresent();
    }

    private Map<String, String> getClaims(Object proveedor) {
    Map<String, String> claims = new HashMap<>();

    if (proveedor instanceof ProveedorEntity p) {
        claims.put("nif", p.getNif());
        claims.put("proveedorId", p.getId().toString());
        claims.put("rol", p.getRol());
    } else if (proveedor instanceof ProveedorEntityDes pd) {
        claims.put("nif", pd.getNif());
        claims.put("proveedorId", pd.getId().toString());
        claims.put("rol", pd.getRol());
    } else {
        throw new RuntimeException("Tipo de proveedor no reconocido");
    }

    return claims;
}


    public String getToken(String nif, Long proveedorId) {
        Object proveedor = proveedorService.findById(proveedorId)
                .filter(p -> {
                    if (p instanceof ProveedorEntity pe) {
                        return pe.getNif().trim().equalsIgnoreCase(nif.trim());
                    } else if (p instanceof ProveedorEntityDes pd) {
                        return pd.getNif().trim().equalsIgnoreCase(nif.trim());
                    }
                    return false;
                })
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ese NIF"));

        return JWTHelper.generateToken(getClaims(proveedor));
    }

    public ProveedorEntity getProveedorFromToken() {
        Object proveedorIdAttr = oHttpServletRequest.getAttribute("proveedorId");
        if (proveedorIdAttr == null)
            throw new UnauthorizedAccessException("No hay proveedor en la sesión");
        Long proveedorId = Long.parseLong(proveedorIdAttr.toString());
        Object proveedor = proveedorService.findById(proveedorId)
                .orElseThrow(() -> new UnauthorizedAccessException("Proveedor no encontrado"));

        if (proveedor instanceof ProveedorEntity p)
            return p;
        if (proveedor instanceof ProveedorEntityDes pDes) {
            // Cast de seguridad
            ProveedorEntity adaptado = new ProveedorEntity();
            adaptado.setId(pDes.getId());
            adaptado.setEmail(pDes.getEmail());
            adaptado.setNif(pDes.getNif());
            adaptado.setPassword(pDes.getPassword());
            adaptado.setRol(pDes.getRol());
            return adaptado;
        }
        throw new RuntimeException("Entidad inesperada");
    }

    public boolean isSessionActive() {
        return oHttpServletRequest.getAttribute("nif") != null;
    }
}