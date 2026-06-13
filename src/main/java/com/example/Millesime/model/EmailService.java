package com.example.Millesime.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void enviarEmailRedefinicaoSenha(String para, String linkRedefinicao) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(fromAddress);
        mensagem.setTo(para);
        mensagem.setSubject("Redefinição de Senha - Millésime");
        mensagem.setText("""
                Olá,

                Recebemos uma solicitação para redefinir sua senha. Clique no link abaixo para criar uma nova senha:
                %s

                Se você não solicitou esta alteração, ignore esta mensagem.

                Atenciosamente,
                Equipe Millésime
                """.formatted(linkRedefinicao));
        mailSender.send(mensagem);
    }
}
