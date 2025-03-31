package es.familycash.proveedores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProveedoresApplication {

	public static void main(String[] args) {
		System.out.println("🔍 Working dir: " + System.getProperty("user.dir"));

		SpringApplication.run(ProveedoresApplication.class, args);
	}

}
