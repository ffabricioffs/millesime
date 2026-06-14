package com.example.Millesime.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void enviarEmailRedefinicaoSenha(String para, String linkRedefinicao) {
        try {
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
            log.info("E-mail de redefinição enviado para {}", para);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", para, e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar e-mail de redefinição.", e);
        }
    }
}
