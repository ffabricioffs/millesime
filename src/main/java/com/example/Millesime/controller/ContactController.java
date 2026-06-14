package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ContactController {

    @PostMapping("/enviar-contato")
    public String enviarContato(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String mensagem,
            @RequestParam(required = false) String newsletter,
            @RequestParam(required = false) String privacidade,
            RedirectAttributes redirectAttributes) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
            || assunto == null || assunto.isBlank()
            || mensagem == null || mensagem.isBlank() || privacidade == null) {
            redirectAttributes.addFlashAttribute("contactError",
                "Preencha os campos obrigatórios e concorde com a Política de Privacidade.");
            return "redirect:/contato";
        }

        redirectAttributes.addFlashAttribute("contactSuccess",
            "Mensagem enviada com sucesso. Em breve retornaremos! Obrigado pelo contato.");
        return "redirect:/contato";
    }

    @PostMapping("/newsletter")
    public String newsletter(@RequestParam String email, RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("newsletterError",
                "Digite um e-mail válido para receber nossas novidades.");
            return "redirect:" + getReferer(request);
        }

        redirectAttributes.addFlashAttribute("newsletterSuccess",
            "Obrigado! Você foi inscrito para receber nossas novidades.");
        return "redirect:" + getReferer(request);
    }

    private String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer != null ? referer : "/";
    }
}
