package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final ClienteService clienteService;

    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "E-mail ou senha inválidos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Você saiu do sistema.");
        }
        model.addAttribute("pageTitle", "Entrar - Millésime");
        return "login";
    }

    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("pageTitle", "Acesso - Millésime");
        return "account";
    }

    @GetMapping("/conta")
    public String accountRedirect(HttpSession session) {
        if (session.getAttribute("clienteLogado") != null) {
            return "redirect:/conta/editar";
        }
        return "redirect:/login";
    }

    @GetMapping("/conta/editar")
    public String editProfile(Model model, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente == null) {
            return "redirect:/login";
        }
        try {
            Cliente clienteAtualizado = clienteService.buscarPorId(cliente.getId());
            model.addAttribute("cliente", clienteAtualizado);
            model.addAttribute("pageTitle", "Editar Perfil - Millésime");
            return "conta-editar";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/conta/editar")
    public String saveProfile(Cliente clienteForm, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente == null) {
            return "redirect:/login";
        }
        try {
            clienteForm.setId(cliente.getId());
            clienteService.atualizarCadastro(clienteForm);

            Cliente clienteAtualizado = clienteService.buscarPorId(cliente.getId());
            session.setAttribute("clienteLogado", clienteAtualizado);
            redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/conta/editar";
    }

    @GetMapping("/minhaconta")
    public String accountRedirectLegacy() {
        return "redirect:/account";
    }
}
