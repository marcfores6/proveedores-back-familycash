package es.familycash.proveedores.helper;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FtpUploader {

    private final String servidor = "ftp.proveedores.familycash.es";
    private final int puerto = 21;
    private final String usuario = "sistemas@grupofamily.es";      // <- cámbialo
    private final String contraseña = "DinahostingSistemas32$"; // <- cámbialo
    private final String rutaRemota = "/www/assets/";

    public String subirArchivo(MultipartFile archivo, String nombreDestino) throws IOException {
        FTPClient ftp = new FTPClient();

        try (InputStream inputStream = archivo.getInputStream()) {
            ftp.connect(servidor, puerto);
            ftp.login(usuario, contraseña);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String rutaFinal = rutaRemota + nombreDestino;
            boolean subido = ftp.storeFile(rutaFinal, inputStream);

            if (!subido) {
                throw new IOException("No se pudo subir el archivo al servidor FTP.");
            }

            return "https://proveedores.familycash.es/assets/" + nombreDestino;
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }
}

