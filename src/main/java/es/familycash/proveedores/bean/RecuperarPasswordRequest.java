package es.familycash.proveedores.bean;

public class RecuperarPasswordRequest {
    private String nif;
    private Long proveedorId;

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }
}
