package es.familycash.proveedores.helper;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FtpUploader {

    private final String servidor = "proveedores.familycash.es";
    private final int puerto = 21;
    private final String usuario = "sistemas@grupofamily.es";
    private final String contraseña = "DinahostingSistemas32$";
    private final String rutaBase = "/www/assets/";
    private final String urlBase = "https://proveedores.familycash.es/assets/";

    public String subirArchivo(MultipartFile archivo, String nombreArchivo, String subcarpetaRelativa)
            throws IOException {
        FTPClient ftp = new FTPClient();

        try (InputStream inputStream = archivo.getInputStream()) {
            ftp.connect(servidor, puerto);
            ftp.login(usuario, contraseña);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // Crear carpetas si no existen
            String rutaCompleta = rutaBase + subcarpetaRelativa;
            String[] carpetas = rutaCompleta.split("/");
            String pathAcumulado = "";
            for (String carpeta : carpetas) {
                if (!carpeta.isBlank()) {
                    pathAcumulado += "/" + carpeta;
                    ftp.makeDirectory(pathAcumulado); // crear si no existe
                }
            }
            ftp.changeWorkingDirectory(pathAcumulado); // cambiar al final

            boolean subido = ftp.storeFile(nombreArchivo, inputStream);
            if (!subido) {
                throw new IOException("No se pudo subir el archivo al servidor FTP.");
            }

            return urlBase + subcarpetaRelativa + "/" + nombreArchivo;
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

}
