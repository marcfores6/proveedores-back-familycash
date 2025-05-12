package es.familycash.proveedores.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImagePathResolver {

    private static final String BASE_FOLDER = "C:/Users/mfores/Desktop/proveedores intranet/proveedores-back-familycash/proveedores/imagenes-familycash/images";

    public static class ImagePath {
        public final Path absolutePath; // Ruta física en disco
        public final String relativeUrl; // Ruta para la BD / frontend

        public ImagePath(Path absolutePath, String relativeUrl) {
            this.absolutePath = absolutePath;
            this.relativeUrl = relativeUrl;
        }
    }

    public static ImagePath generate(String entityType, Long entityId, String originalFilename) {
        String relativeUrl = "/images/" + entityType + "/" + entityId + "/" + originalFilename;
        Path absolutePath = Paths.get(BASE_FOLDER, entityType, String.valueOf(entityId), originalFilename);
    
        try {
            Files.createDirectories(absolutePath.getParent());
            System.out.println("📁 Carpeta creada: " + absolutePath.getParent());
        } catch (IOException e) {
            System.err.println("❌ Error creando carpeta: " + e.getMessage());
        }
    
        return new ImagePath(absolutePath, relativeUrl);
    }
    

    public static Path getFolderPath(String entityType, Long entityId) {
        return Paths.get(BASE_FOLDER, entityType, String.valueOf(entityId));
    }

    public static void saveImage(Path ruta, byte[] imagenBytes) throws IOException {
        Files.write(ruta, imagenBytes);
        System.out.println("✅ Imagen guardada en: " + ruta);
    }
}
