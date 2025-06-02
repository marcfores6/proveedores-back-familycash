package es.familycash.proveedores.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDataBean {
    private String nif;
    private String password;
    private Long proveedorId;
    private String rol; 
}

