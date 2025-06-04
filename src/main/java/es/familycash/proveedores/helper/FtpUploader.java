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
    private final String rutaRemotaBase = "/www/assets/";


    public String subirArchivo(MultipartFile archivo, String nombreDestino) throws IOException {
    return subirArchivo(archivo, nombreDestino, "/www/assets/");
}

    public String subirArchivo(MultipartFile archivo, String nombreDestino, String rutaRemota) throws IOException {
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
