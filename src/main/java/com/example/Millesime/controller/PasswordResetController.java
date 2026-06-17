package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/reset-password")
public class PasswordResetController {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);

    private final ClienteService clienteService;

    public PasswordResetController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String resetPassword(Model model) {
        model.addAttribute("pageTitle", "Recuperar Senha - Millésime");
        return "reset-password";
    }

    @PostMapping
    public String resetPasswordSubmit(@RequestParam(required = false) String email,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Informe um e-mail válido para recuperar a senha.");
            return "redirect:/reset-password";
        }

        String baseUrl = String.format("%s://%s%s", request.getScheme(), request.getServerName(),
            request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
        try {
            clienteService.criarTokenRedefinicaoSenha(email, baseUrl);
            redirectAttributes.addFlashAttribute("message",
                "Se o e-mail estiver cadastrado, você receberá instruções de recuperação em breve.");
        } catch (Exception e) {
            log.error("Erro ao criar token de redefinicao de senha para {}", email, e);
            redirectAttributes.addFlashAttribute("error",
                "Não foi possível processar sua solicitação no momento. Tente novamente mais tarde.");
        }
        return "redirect:/reset-password";
    }

    @GetMapping("/confirm")
    public String confirmResetPassword(@RequestParam(required = false) String token, Model model,
                                        RedirectAttributes redirectAttributes) {
        if (token == null) {
            Object flashToken = redirectAttributes.getFlashAttributes().get("token");
            if (flashToken instanceof String) {
                token = (String) flashToken;
            }
        }
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Token de redefinição inválido.");
            return "redirect:/reset-password";
        }

        try {
            Cliente cliente = clienteService.validarTokenRedefinicaoSenha(token);
            model.addAttribute("pageTitle", "Redefinir Senha - Millésime");
            model.addAttribute("token", token);
            model.addAttribute("cliente", cliente);
            return "reset-password-confirm";
        } catch (Exception e) {
            log.error("Erro ao validar token de redefinicao de senha", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao redefinir senha.");
            return "redirect:/reset-password";
        }
    }

    @PostMapping("/confirm")
    public String confirmResetPasswordSubmit(@RequestParam(required = false) String token,
                                               @RequestParam(required = false) String novaSenha,
                                               RedirectAttributes redirectAttributes) {
        try {
            clienteService.redefinirSenha(token, novaSenha);
            redirectAttributes.addFlashAttribute("message",
                "Senha redefinida com sucesso. Faça login com sua nova senha.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Erro ao redefinir senha com token", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao redefinir senha.");
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/reset-password/confirm";
        }
    }
}
