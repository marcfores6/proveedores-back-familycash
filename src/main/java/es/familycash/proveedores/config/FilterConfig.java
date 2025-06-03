package es.familycash.proveedores.config;

import es.familycash.proveedores.filter.JWTFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class FilterConfig {

    // 🔵 Bean del CORSFilter que necesitas
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("*")); // O mejor: tu dominio frontend
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    // 🟡 Registro para que el filtro CORS se aplique primero
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsFilter corsFilter) {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        registration.setOrder(0); // 👉 CORS primero
        return registration;
    }

    // 🔐 Filtro JWT (segundo en orden)
    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterRegistration(JWTFilter jwtFilter) {
        FilterRegistrationBean<JWTFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setOrder(1); // 👉 JWT después
        return registration;
    }
}
