package es.familycash.proveedores.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.familycash.proveedores.entity.ProveedorEntity;
import es.familycash.proveedores.entity.ProveedorImagenEntity;
import es.familycash.proveedores.entity.TipoproveedorEntity;
import es.familycash.proveedores.exception.ResourceNotFoundException;
import es.familycash.proveedores.exception.UnauthorizedAccessException;
import es.familycash.proveedores.helper.ImagePathResolver;
import es.familycash.proveedores.repository.ProveedorImagenRepository;
import es.familycash.proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository oProveedorRepository;

    @Autowired
    ProveedorImagenRepository oProveedorImagenRepository;

    @Autowired
    AuthService oAuthService;

    public Page<ProveedorEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oProveedorRepository
                    .findByEmpresaContainingOrEmailContainingOrPasswordContaining(
                            filter.get(), filter.get(), filter.get(),
                            oPageable);
        } else {
            return oProveedorRepository.findAll(oPageable);
        }
    }

    public ProveedorEntity get(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    public Long count() {
        return oProveedorRepository.count();
    }

    public Long delete(Long id) {
        oProveedorRepository.deleteById(id);
        return 1L;
    }

    public ProveedorEntity create(ProveedorEntity oProveedorEntity) {
        return oProveedorRepository.save(oProveedorEntity);
    }

    public ResponseEntity<?> createProveedor(
            String empresa,
            String email,
            String password,
            TipoproveedorEntity tipoproveedor,
            List<MultipartFile> imagen,
            List<String> imagenUrl) {

        try {
            if (empresa == null || empresa.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    tipoproveedor == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Todos los campos son obligatorios."));
            }

            ProveedorEntity nuevoProveedor = new ProveedorEntity();
            nuevoProveedor.setEmpresa(empresa);
            nuevoProveedor.setEmail(email);
            nuevoProveedor.setPassword(password);
            nuevoProveedor.setTipoproveedor(tipoproveedor);

            ProveedorEntity proveedorGuardado = oProveedorRepository.save(nuevoProveedor);

            // Guardar imágenes por archivo
            if (imagen != null) {
                for (MultipartFile file : imagen) {
                    if (file != null && !file.isEmpty()) {
                        ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                                "proveedor", proveedorGuardado.getId(), file.getOriginalFilename());

                        Files.createDirectories(ruta.absolutePath.getParent());
                        Files.write(ruta.absolutePath, file.getBytes());

                        ProveedorImagenEntity imagenEntity = new ProveedorImagenEntity();
                        imagenEntity.setProveedor(proveedorGuardado);
                        imagenEntity.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));

                        oProveedorImagenRepository.save(imagenEntity);
                    }
                }
            }

            // Guardar imágenes por URL
            if (imagenUrl != null) {
                for (String url : imagenUrl) {
                    if (url != null && !url.trim().isEmpty()) {
                        ProveedorImagenEntity imagenEntity = new ProveedorImagenEntity();
                        imagenEntity.setProveedor(proveedorGuardado);
                        imagenEntity.setImagenUrl(url.trim());

                        oProveedorImagenRepository.save(imagenEntity);
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorGuardado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al guardar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }

    public ResponseEntity<?> updateProveedor(
            Long id,
            String empresa,
            String email,
            String password,
            TipoproveedorEntity tipoproveedor,
            List<MultipartFile> imagenes,
            List<String> imagenesUrl) {
        try {
            ProveedorEntity proveedorExistente = oProveedorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

            proveedorExistente.setEmpresa(empresa);
            proveedorExistente.setEmail(email);
            proveedorExistente.setPassword(password);
            proveedorExistente.setTipoproveedor(tipoproveedor);

            ProveedorEntity proveedorActualizado = oProveedorRepository.save(proveedorExistente);

            // 💣 Eliminar imágenes anteriores si deseas sustituir completamente (opcional)
            oProveedorImagenRepository.deleteById(proveedorExistente.getId());

            // 📁 Guardar nuevas imágenes por archivo
            if (imagenes != null) {
                for (MultipartFile file : imagenes) {
                    if (file != null && !file.isEmpty()) {
                        ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                                "proveedor", proveedorExistente.getId(), file.getOriginalFilename());

                        Files.createDirectories(ruta.absolutePath.getParent());
                        Files.write(ruta.absolutePath, file.getBytes());

                        ProveedorImagenEntity nueva = new ProveedorImagenEntity();
                        nueva.setProveedor(proveedorExistente);
                        nueva.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));

                        oProveedorImagenRepository.save(nueva);
                    }
                }
            }

            // 🌐 Guardar imágenes por URL
            if (imagenesUrl != null) {
                for (String url : imagenesUrl) {
                    if (url != null && !url.trim().isEmpty()) {
                        ProveedorImagenEntity nueva = new ProveedorImagenEntity();
                        nueva.setProveedor(proveedorExistente);
                        nueva.setImagenUrl(url.trim());

                        oProveedorImagenRepository.save(nueva);
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(proveedorActualizado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al guardar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
        }
    }

    private void guardarImagen(ProveedorEntity proveedor, MultipartFile imagen, String imagenUrl) throws IOException {
        if (imagen != null && !imagen.isEmpty()) {
            ImagePathResolver.ImagePath ruta = ImagePathResolver.generate(
                    "proveedor", proveedor.getId(), imagen.getOriginalFilename());

            Files.createDirectories(ruta.absolutePath.getParent());
            Files.write(ruta.absolutePath, imagen.getBytes());

            ProveedorImagenEntity nueva = new ProveedorImagenEntity();
            nueva.setProveedor(proveedor);
            nueva.setImagenUrl("/" + ruta.relativeUrl.replace("\\", "/"));
            oProveedorImagenRepository.save(nueva);
        } else if (imagenUrl != null && !imagenUrl.isBlank()) {
            ProveedorImagenEntity nueva = new ProveedorImagenEntity();
            nueva.setProveedor(proveedor);
            nueva.setImagenUrl(imagenUrl);
            oProveedorImagenRepository.save(nueva);
        }
    }

    public void deleteImagen(Long id) {
        oProveedorImagenRepository.deleteById(id);
    }

    public Long deleteAll() {
        oProveedorRepository.deleteAll();
        return this.count();
    }

    public ProveedorEntity findById(Long id) {
        return oProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    public ProveedorEntity getByEmail(String email) {
        ProveedorEntity oProveedorEntity = oProveedorRepository.findByEmailWithTipo(email)
                .orElseThrow(() -> new ResourceNotFoundException("El Proveedor con email " + email + " no existe"));
        if (oAuthService.isClientWithItsOwnData(oProveedorEntity.getId()) || oAuthService.isAdmin()) {
            return oProveedorEntity;
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver el Proveedor");
        }
    }


    
}
