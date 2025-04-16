package es.familycash.proveedores.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecuperacionEmail(String toEmail, String token) {
        String link = "http://localhost:4200/restablecer-password?token=" + token;
        String subject = "Recuperación de contraseña - FamilyCash Proveedores";
        String body = "Hola, haz clic en el siguiente enlace para restablecer tu contraseña:\n\n" + link + "\n\nEste enlace expirará en 30 minutos.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

    

