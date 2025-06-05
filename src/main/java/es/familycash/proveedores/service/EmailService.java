package es.familycash.proveedores.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecuperacionEmail(String toEmail, String token) {
        try {
            String link = "http://proveedores.familycash.es/restablecer-password?token=" + token;
            String subject = "Recuperación de contraseña - FamilyCash Proveedores";

            String body = "<div style='font-family: Arial, sans-serif;'>" +
                    "<img src='cid:logoFamily' alt='FamilyCash' style='max-width: 200px; margin-bottom: 20px;'/>" +
                    "<h2>Recuperación de contraseña</h2>" +
                    "<p>Hola, haz clic en el siguiente enlace para restablecer tu contraseña:</p>" +
                    "<p><a href='" + link + "' style='color: #2c7be5; font-weight: bold;'>Restablecer contraseña</a></p>" +
                    "<p style='color: #666;'>Este enlace expirará en 30 minutos.</p>" +
                    "<br><hr><small>Este correo ha sido generado automáticamente por FamilyCash Proveedores.</small></div>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("avisosweb@familycash.es");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            // Añadir la imagen embebida
            ClassPathResource image = new ClassPathResource("/static/assets/logo-family-correo.png");
            helper.addInline("logoFamily", image);

            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
