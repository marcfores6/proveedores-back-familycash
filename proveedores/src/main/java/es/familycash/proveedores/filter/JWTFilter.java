package es.familycash.proveedores.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.familycash.proveedores.service.JWTService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter implements Filter {

    @Autowired
    JWTService JWTHelper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        String tokenHeader = httpRequest.getHeader("Authorization");

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response); // Dejamos pasar, el propio endpoint protegerá si es necesario
            return;
        }

        String token = tokenHeader.substring(7);

        try {
            String nif = JWTHelper.validateToken(token);
            if (nif == null) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido o expirado");
                return;
            }
        
            Claims claims = JWTHelper.getAllClaimsFromToken(token);
            httpRequest.setAttribute("nif", claims.get("nif", String.class));
            httpRequest.setAttribute("proveedorId", claims.get("proveedorId", String.class));

            String rol = claims.get("rol", String.class);
            httpRequest.setAttribute("rol", rol);
        
        } catch (Exception e) {
            e.printStackTrace(); // 🔍 Importante para que veas el error exacto en consola
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido o expirado");
            return;
        }
        
        

        chain.doFilter(request, response);
    }
}