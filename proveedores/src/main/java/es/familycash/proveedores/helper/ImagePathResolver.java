package es.familycash.proveedores.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ImagePathResolver {

    private static final String BASE_FOLDER = "src/main/resources/static/images";

    public static class ImagePath {
        public final Path absolutePath;   // Ruta física donde guardar el archivo
        public final String relativeUrl;  // Ruta relativa para servir desde el frontend

        public ImagePath(Path absolutePath, String relativeUrl) {
            this.absolutePath = absolutePath;
            this.relativeUrl = relativeUrl;
        }
    }

    /**
     * Genera la ruta física (en el sistema de archivos) y la relativa (para la BD o frontend)
     * @param entityType tipo de entidad (ej: proveedor, producto)
     * @param entityId id de la entidad
     * @param originalFilename nombre del archivo original
     * @return objeto con ruta física y relativa
     */
    public static ImagePath generate(String entityType, Long entityId, String originalFilename) {
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        // Ruta relativa para la URL que se almacena en la base de datos
        String relativeUrl = "images/" + entityType + "/" + entityId + "/" + fileName;

        // Ruta absoluta para guardar la imagen
        Path absolutePath = Paths.get(BASE_FOLDER, entityType, String.valueOf(entityId), fileName);

        // Crear el directorio si no existe
        try {
            Files.createDirectories(absolutePath.getParent());
            System.out.println("Directorio creado: " + absolutePath.getParent().toString());  // Depuración
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }

        return new ImagePath(absolutePath, relativeUrl);
    }

    /**
     * Devuelve el Path a una carpeta donde se espera encontrar las imágenes para esa entidad
     */
    public static Path getFolderPath(String entityType, Long entityId) {
        return Paths.get(BASE_FOLDER, entityType, String.valueOf(entityId));
    }

    /**
     * Guarda una imagen en el disco en la ruta especificada
     * @param ruta Ruta absoluta donde se almacenará la imagen
     * @param imagenBytes El contenido binario de la imagen
     * @throws IOException
     */
    public static void saveImage(Path ruta, byte[] imagenBytes) throws IOException {
        // Verificar si la carpeta existe y luego escribir la imagen
        Files.write(ruta, imagenBytes);
        System.out.println("Imagen guardada en: " + ruta.toString());  // Depuración
    }
}
