package es.familycash.proveedores.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    AuthService oAuthService;

    public Page<ProveedorEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oProveedorRepository.findByDescripcionContaining(filter.get(), oPageable);
        } else {
            return oProveedorRepository.findAll(oPageable);
        }
    }

    public ProveedorEntity save(ProveedorEntity oProveedor) {
        return oProveedorRepository.save(oProveedor);
    }

    public ProveedorEntity update(ProveedorEntity oProveedor) {
        return oProveedorRepository.save(oProveedor);
    }

    public ProveedorEntity get(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
    }

    public Long count() {
        return oProveedorRepository.count();
    }

    public Long delete(Long id) {
        oProveedorRepository.deleteById(id);
        return 1L;
    }

    public Long deleteAll() {
        oProveedorRepository.deleteAll();
        return this.count();
    }

    public ProveedorEntity findById(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    public ProveedorEntity getByNif(String nif) {
        return oProveedorRepository.findByNif(nif)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con NIF: " + nif));
    }

    public ProveedorEntity getProveedorByNif(String nif) {
        return oProveedorRepository.findByNif(nif)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con NIF: " + nif));
    }

    public ProveedorEntity getProveedorFromToken() {
        return oAuthService.getProveedorFromToken();
    }

    public void updatePassword(Long id, String newPassword) {
        if (!isValidPassword(newPassword)) {
            throw new RuntimeException("La contraseña no cumple con los requisitos de seguridad.");
        }
    
        ProveedorEntity proveedor = oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    
        proveedor.setPassword(hashPassword(newPassword));
        oProveedorRepository.save(proveedor);
    }
    
    
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
    
    private String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error al hashear la contraseña", e);
    }
}
    

}
