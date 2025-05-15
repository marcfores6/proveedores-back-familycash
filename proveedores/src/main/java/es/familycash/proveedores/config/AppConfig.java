package es.familycash.proveedores.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AppConfig {

    @Value("${app.environment}")
    private String environment;

    public boolean isDev() {
        return "dev".equalsIgnoreCase(environment);
    }

    public boolean isProd() {
        return "prod".equalsIgnoreCase(environment);
    }

    public String getEnvironment() {
        return environment;
    }

    @PostConstruct
    public void init() {
        System.out.println("🌍 AppConfig iniciado. Entorno actual: " + environment);
    }
}
