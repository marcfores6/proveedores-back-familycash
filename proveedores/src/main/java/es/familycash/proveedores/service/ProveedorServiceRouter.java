package es.familycash.proveedores.service;

import es.familycash.proveedores.config.AppConfig;
import es.familycash.proveedores.config.RequestContext;
import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.entity.ProveedorEntityDes;
import es.familycash.proveedores.repository.ProveedorRepository;
import es.familycash.proveedores.repository.ProveedorRepositoryDes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class ProveedorServiceRouter {

    @Autowired
    RequestContext oRequestContext;

    @Autowired
    private ProveedorRepository repoProd;

    @Autowired
    private ProveedorRepositoryDes repoDes;

    private boolean isDev() {
        return oRequestContext.isDev();
    }

    public Optional<?> findByNifAndPassword(String nif, String password) {
        if (isDev()) {
            return repoDes.findByNifAndPassword(nif, password);
        } else {
            return repoProd.findByNifAndPassword(nif, password);
        }
    }

    public Optional<?> findByTokenRecuperacion(String token) {
        return isDev() ? repoDes.findByTokenRecuperacion(token)
                : repoProd.findByTokenRecuperacion(token);
    }

    public int actualizarTokenRecuperacion(String token, LocalDateTime expiracion, Long id) {
        return isDev() ? repoDes.actualizarTokenRecuperacion(token, expiracion, id)
                : repoProd.actualizarTokenRecuperacion(token, expiracion, id);
    }

    public Optional<?> findById(Long id) {
        return isDev() ? repoDes.findById(id) : repoProd.findById(id);
    }

    public void save(Object proveedor) {
        if (isDev()) {
            repoDes.save((ProveedorEntityDes) proveedor);
        } else {
            repoProd.save((ProveedorEntity) proveedor);
        }
    }

    public List<?> findAll() {
        return isDev() ? repoDes.findAll() : repoProd.findAll();
    }

    public Optional<?> findByNif(String nif) {
        if (isDev()) {
            System.out.println("🔄 Usando repo DES (desarrollo)");
            return repoDes.findByNif(nif);
        } else {
            System.out.println("🔄 Usando repo PROD (producción)");
            return repoProd.findByNif(nif);
        }
    }

    public Page<? extends Object> getPage(Pageable pageable, Optional<String> filter) {
        if (isDev()) {
            if (filter.isPresent()) {
                return repoDes.findByDescripcionContaining(filter.get(), pageable);
            } else {
                return repoDes.findAll(pageable);
            }
        } else {
            if (filter.isPresent()) {
                return repoProd.findByDescripcionContaining(filter.get(), pageable);
            } else {
                return repoProd.findAll(pageable);
            }
        }
    }

    public Long count() {
        return isDev() ? repoDes.count() : repoProd.count();
    }

    public Long delete(Long id) {
        if (isDev()) {
            repoDes.deleteById(id);
        } else {
            repoProd.deleteById(id);
        }
        return id;
    }

    public Long deleteAll() {
        if (isDev()) {
            long count = repoDes.count();
            repoDes.deleteAll();
            return count;
        } else {
            long count = repoProd.count();
            repoProd.deleteAll();
            return count;
        }
    }

    public void updatePassword(String nif, String newPassword) {
        Optional<?> opt = findByNif(nif);
        if (opt.isPresent()) {
            Object proveedor = opt.get();
            if (proveedor instanceof ProveedorEntity p) {
                p.setPassword(newPassword);
                repoProd.save(p);
            } else if (proveedor instanceof ProveedorEntityDes pDes) {
                pDes.setPassword(newPassword);
                repoDes.save(pDes);
            }
        }
    }

    public void updateEmail(String nif, String nuevoEmail) {
        List<?> lista = isDev() ? repoDes.findAllByNif(nif) : repoProd.findAllByNif(nif);

        if (lista.isEmpty()) {
            throw new RuntimeException("❌ No se ha encontrado ningún proveedor con ese NIF.");
        }

        if (lista.size() > 1) {
            throw new RuntimeException(
                    "❌ Se han encontrado múltiples proveedores con el mismo NIF. No se puede actualizar el email de forma segura.");
        }

        Object proveedor = lista.get(0);

        if (proveedor instanceof ProveedorEntity p) {
            p.setEmail(nuevoEmail);
            repoProd.save(p);
        } else if (proveedor instanceof ProveedorEntityDes pDes) {
            pDes.setEmail(nuevoEmail);
            repoDes.save(pDes);
        }
    }

    public void updateEmailById(Long id, String nuevoEmail) {
        Optional<?> opt = findById(id);
        if (opt.isPresent()) {
            Object proveedor = opt.get();
            if (proveedor instanceof ProveedorEntity p) {
                p.setEmail(nuevoEmail);
                repoProd.save(p);
            } else if (proveedor instanceof ProveedorEntityDes pDes) {
                pDes.setEmail(nuevoEmail);
                repoDes.save(pDes);
            }
        } else {
            throw new RuntimeException("Proveedor no encontrado con id: " + id);
        }
    }

}
