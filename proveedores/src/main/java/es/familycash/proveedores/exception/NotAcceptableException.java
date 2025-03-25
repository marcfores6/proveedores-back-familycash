package es.familycash.proveedores.exception;

public class NotAcceptableException extends RuntimeException {
    public NotAcceptableException(String mensaje) {
        super(mensaje);
    }
}
