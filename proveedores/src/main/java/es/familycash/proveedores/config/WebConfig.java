package es.familycash.proveedores.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("file:///C:/Users/mfores/Desktop/proveedores intranet/proveedores-back-familycash/proveedores/imagenes-familycash/images/")
            .setCachePeriod(3600);     }
}
