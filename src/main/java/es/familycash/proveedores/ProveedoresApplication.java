package es.familycash.proveedores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "es.familycash.proveedores")
public class ProveedoresApplication {

	public static void main(String[] args) {
		System.out.println("🔍 Working dir: " + System.getProperty("user.dir"));

		SpringApplication.run(ProveedoresApplication.class, args);
	}

}
// 👇 Esto añadelo justo debajo de la clase principal
@Component
class StartupLogger {

	@Value("${app.environment}")
	private String environment;

	@PostConstruct
	public void logEnvironment() {
		System.out.println("🌍 ENTORNO ACTUAL INICIALIZADO --> app.environment = " + environment);
	}
}
