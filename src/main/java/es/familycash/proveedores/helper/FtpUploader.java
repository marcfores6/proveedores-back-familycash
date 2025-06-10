package es.familycash.proveedores.helper;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FtpUploader {

    @Value("${ftp.host}")
    private String servidor;

    @Value("${ftp.port}")
    private int puerto;

    @Value("${ftp.user}")
    private String usuario;

    @Value("${ftp.pass}")
    private String contraseña;

    @Value("${ftp.path}")
    private String rutaBase;

    @Value("${ftp.urlBase}")
    private String urlBase;

    public String subirArchivo(MultipartFile archivo, String nombreDestino, String subcarpeta) throws IOException {
        FTPClient ftp = new FTPClient();

        try (InputStream inputStream = archivo.getInputStream()) {
            System.out.println("Conectando al servidor FTP: " + servidor + ":" + puerto);
            ftp.connect(servidor, puerto);

            boolean login = ftp.login(usuario, contraseña);
            if (!login) {
                throw new IOException("No se pudo autenticar en el servidor FTP");
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String rutaFinal = rutaBase + subcarpeta + "/" + nombreDestino;
            System.out.println("Ruta destino en FTP: " + rutaFinal);

            crearDirectoriosRecursivos(ftp, rutaBase + subcarpeta);

            boolean subido = ftp.storeFile(rutaFinal, inputStream);

            if (!subido) {
                throw new IOException("No se pudo subir el archivo al servidor FTP.");
            }

            return urlBase + subcarpeta + "/" + nombreDestino;
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }

    public void eliminarArchivo(String rutaRemotaCompleta) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            System.out.println("Conectando para eliminar archivo: " + rutaRemotaCompleta);
            ftp.connect(servidor, puerto);
            ftp.login(usuario, contraseña);
            ftp.enterLocalPassiveMode();

            boolean eliminado = ftp.deleteFile(rutaRemotaCompleta);

            if (!eliminado) {
                throw new IOException("No se pudo eliminar el archivo del servidor FTP: " + rutaRemotaCompleta);
            }

        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }

    private void crearDirectoriosRecursivos(FTPClient ftp, String ruta) throws IOException {
        String[] directorios = ruta.split("/");
        String rutaActual = "";
        for (String dir : directorios) {
            if (dir == null || dir.trim().isEmpty())
                continue;
            rutaActual += "/" + dir;
            if (!ftp.changeWorkingDirectory(rutaActual)) {
                // Si no existe, la creamos
                boolean creada = ftp.makeDirectory(rutaActual);
                if (creada) {
                    System.out.println("📁 Carpeta creada en FTP: " + rutaActual);
                } else {
                    System.out.println("⚠️ No se pudo crear la carpeta (quizás ya existe): " + rutaActual);
                }
                // Cambiar a la nueva carpeta
                ftp.changeWorkingDirectory(rutaActual);
            }
        }
        // Volver a la raíz después (opcional)
        ftp.changeWorkingDirectory("/");
    }

}
