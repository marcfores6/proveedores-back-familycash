package es.familycash.proveedores.exception;

public class ResourceNotModifiedException extends RuntimeException {
    public ResourceNotModifiedException(String mensaje) {
        super(mensaje);
    }
}
