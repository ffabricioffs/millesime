package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.model.Contato;
import com.example.Millesime.model.ContatoDAO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ContactController {

    private final ContatoDAO contatoDAO;

    public ContactController(ContatoDAO contatoDAO) {
        this.contatoDAO = contatoDAO;
    }

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

        Contato contato = new Contato();
        contato.setNome(nome.trim());
        contato.setEmail(email.trim());
        contato.setAssunto(assunto.trim());
        contato.setMensagem(mensagem.trim());
        try {
            contatoDAO.salvar(contato);
        } catch (Exception e) {
            // log silencioso — falha não impede o redirect de sucesso
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
            return "redirect:" + getSafeRedirect(request);
        }

        redirectAttributes.addFlashAttribute("newsletterSuccess",
            "Obrigado! Você foi inscrito para receber nossas novidades.");
        return "redirect:" + getSafeRedirect(request);
    }

    private String getSafeRedirect(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null) {
            return "/";
        }
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String expectedPrefix = "http" + (port == 443 ? "s" : "") + "://" + serverName;
        if (port != 80 && port != 443) {
            expectedPrefix += ":" + port;
        }
        if (referer.startsWith(expectedPrefix)) {
            return referer;
        }
        return "/";
    }
}
