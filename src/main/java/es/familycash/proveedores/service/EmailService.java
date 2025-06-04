package es.familycash.proveedores.service;

import org.springframework.beans.factory.annotation.Autowired;
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
            String body = "Hola, haz clic en el siguiente enlace para restablecer tu contraseña:<br><br>" +
                    "<a href=\"" + link + "\">Restablecer contraseña</a><br><br>Este enlace expirará en 30 minutos.";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("avisosweb@familycash.es"); // 👈 ESTO ES LO QUE HACE FALTA
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
