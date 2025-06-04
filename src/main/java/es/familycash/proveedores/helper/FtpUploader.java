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
            ftp.connect(servidor, puerto);
            ftp.login(usuario, contraseña);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String rutaFinal = rutaBase + subcarpeta + "/" + nombreDestino;
            ftp.makeDirectory(rutaBase + subcarpeta); // intenta crear carpeta si no existe
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
