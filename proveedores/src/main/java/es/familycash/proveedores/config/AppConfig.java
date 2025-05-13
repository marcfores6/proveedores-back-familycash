package es.familycash.proveedores.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${entorno.desarrollo:false}")
    private boolean entornoBase;

    private Boolean entornoTemporal = null;

    public boolean isDesarrollo() {
        return entornoTemporal != null ? entornoTemporal : entornoBase;
    }

    public void setEntornoTemporal(Boolean valor) {
        this.entornoTemporal = valor;
    }

    public void resetEntorno() {
        this.entornoTemporal = null;
    }

    public String getTablaProveedores() {
        return isDesarrollo() ? "LU_PRO_DES" : "LU_PRO";
    }

    public String getTablaProductos() {
        return isDesarrollo() ? "LU_ARA_DES" : "LU_ARA";
    }

    public String getTablaProductoImagen() {
        return isDesarrollo() ? "DT_PRO_IMG_DES" : "producto_imagen";
    }

    public String getTablaProveedoresDocumento() {
        return isDesarrollo() ? "DT_PRO_DOC_DES" : "producto_documento";
    }
}
