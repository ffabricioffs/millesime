package com.example.Millesime.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.model.Contato;
import com.example.Millesime.model.ContatoDAO;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContatoDAO contatoDAO;

    public ContactController(ContatoDAO contatoDAO) {
        this.contatoDAO = contatoDAO;
    }

    @PostMapping("/enviar-contato")
    public Object enviarContato(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String mensagem,
            @RequestParam(required = false) String newsletter,
            @RequestParam(required = false) String privacidade,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        boolean hasError = nome == null || nome.isBlank() || email == null || email.isBlank()
            || assunto == null || assunto.isBlank()
            || mensagem == null || mensagem.isBlank() || privacidade == null;

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            if (hasError) {
                Map<String, Object> json = new HashMap<>();
                json.put("success", false);
                json.put("message", "Preencha os campos obrigatórios e concorde com a Política de Privacidade.");
                return ResponseEntity.badRequest().body(json);
            }
        } else if (hasError) {
            redirectAttributes.addFlashAttribute("contactError",
                "Preencha os campos obrigatórios e concorde com a Política de Privacidade.");
            return "redirect:/contato";
        }

        Contato contato = new Contato();
        contato.setNome(nome.trim());
        contato.setEmail(email.trim());
        contato.setAssunto(assunto.trim());
        contato.setMensagem(mensagem.trim());
        contato.setTelefone(telefone != null ? telefone.trim() : null);
        contato.setNewsletter("sim".equals(newsletter));
        try {
            contatoDAO.salvar(contato);
        } catch (Exception e) {
            log.error("Erro ao salvar contato no banco de dados", e);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                Map<String, Object> json = new HashMap<>();
                json.put("success", false);
                json.put("message", "Erro interno ao enviar mensagem. Tente novamente mais tarde.");
                return ResponseEntity.status(500).body(json);
            }
            redirectAttributes.addFlashAttribute("contactError",
                "Erro interno ao enviar mensagem. Tente novamente mais tarde.");
            return "redirect:/contato";
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            Map<String, Object> json = new HashMap<>();
            json.put("success", true);
            json.put("message", "Mensagem enviada com sucesso. Em breve retornaremos! Obrigado pelo contato.");
            return ResponseEntity.ok(json);
        }

        redirectAttributes.addFlashAttribute("contactSuccess",
            "Mensagem enviada com sucesso. Em breve retornaremos! Obrigado pelo contato.");
        return "redirect:/contato";
    }

    @PostMapping("/newsletter")
    public Object newsletter(@RequestParam(required = false) String email,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                Map<String, Object> json = new HashMap<>();
                json.put("success", false);
                json.put("message", "Digite um e-mail válido para receber nossas novidades.");
                return ResponseEntity.badRequest().body(json);
            }
            redirectAttributes.addFlashAttribute("newsletterError",
                "Digite um e-mail válido para receber nossas novidades.");
            return "redirect:" + getSafeRedirect(request);
        }
        try {
            Contato contato = new Contato();
            contato.setEmail(email.trim());
            contato.setAssunto("newsletter");
            contatoDAO.salvar(contato);
        } catch (Exception e) {
            log.error("Erro ao salvar inscricao newsletter para {}", email, e);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                Map<String, Object> json = new HashMap<>();
                json.put("success", false);
                json.put("message", "Erro ao processar inscrição. Tente novamente.");
                return ResponseEntity.status(500).body(json);
            }
            redirectAttributes.addFlashAttribute("newsletterError",
                "Erro ao processar inscrição. Tente novamente.");
            return "redirect:" + getSafeRedirect(request);
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            Map<String, Object> json = new HashMap<>();
            json.put("success", true);
            json.put("message", "Obrigado! Você foi inscrito para receber nossas novidades.");
            return ResponseEntity.ok(json);
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
