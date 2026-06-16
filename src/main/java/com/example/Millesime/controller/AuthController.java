package com.example.Millesime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.dto.AlterarSenhaRequest;
import com.example.Millesime.dto.ClienteSession;
import com.example.Millesime.dto.PerfilUpdateRequest;
import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final ClienteService clienteService;

    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "E-mail ou senha inv\u00e1lidos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Voc\u00ea saiu do sistema.");
        }
        model.addAttribute("pageTitle", "Entrar - Mill\u00e9sime");
        return "login";
    }

    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("pageTitle", "Acesso - Mill\u00e9sime");
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
    public String editProfile(Model model, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }
        try {
            Cliente cliente = clienteService.buscarPorId(sessionCliente.getId());
            PerfilUpdateRequest request = new PerfilUpdateRequest();
            request.setNomeCompleto(cliente.getNomeCompleto());
            request.setEmail(cliente.getEmail());
            request.setDataNascimento(cliente.getDataNascimento());
            request.setTelefone(cliente.getTelefone());
            request.setNewsletter(cliente.isNewsletter());
            model.addAttribute("perfilRequest", request);
            model.addAttribute("cliente", cliente);
            model.addAttribute("pageTitle", "Editar Perfil - Mill\u00e9sime");
            return "conta-editar";
        } catch (Exception e) {
            log.error("Erro ao carregar edicao de perfil", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao carregar dados. Tente novamente.");
            return "redirect:/login";
        }
    }

    @PostMapping("/conta/editar")
    public String saveProfile(@Valid @ModelAttribute PerfilUpdateRequest request,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Verifique os campos obrigatorios.");
            return "redirect:/conta/editar";
        }
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }
        try {
            Cliente cliente = new Cliente();
            cliente.setId(sessionCliente.getId());
            cliente.setNomeCompleto(request.getNomeCompleto());
            cliente.setEmail(request.getEmail());
            cliente.setDataNascimento(request.getDataNascimento());
            cliente.setTelefone(request.getTelefone());
            cliente.setNewsletter(request.isNewsletter());

            clienteService.atualizarCadastro(cliente);

            Cliente clienteAtualizado = clienteService.buscarPorId(sessionCliente.getId());
            sessionCliente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
            sessionCliente.setEmail(clienteAtualizado.getEmail());
            session.setAttribute("clienteLogado", sessionCliente);
            redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao salvar perfil do usuario {}", sessionCliente.getId(), e);
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar perfil. Tente novamente.");
        }
        return "redirect:/conta/editar";
    }

    @GetMapping("/conta/alterar-senha")
    public String alterarSenha(Model model, HttpSession session) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }
        model.addAttribute("alterarSenhaRequest", new AlterarSenhaRequest());
        model.addAttribute("pageTitle", "Alterar Senha - Mill\u00e9sime");
        return "conta-alterar-senha";
    }

    @PostMapping("/conta/alterar-senha")
    public String alterarSenhaSubmit(@ModelAttribute AlterarSenhaRequest request,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }
        try {
            if (!request.getNovaSenha().equals(request.getConfirmaSenha())) {
                redirectAttributes.addFlashAttribute("error", "A confirma\u00e7\u00e3o da senha n\u00e3o confere.");
                return "redirect:/conta/alterar-senha";
            }

            Cliente cliente = clienteService.buscarPorId(sessionCliente.getId());
            if (!clienteService.verificarSenha(cliente, request.getSenhaAtual())) {
                redirectAttributes.addFlashAttribute("error", "Senha atual incorreta.");
                return "redirect:/conta/alterar-senha";
            }

            clienteService.alterarSenhaDiretamente(cliente, request.getNovaSenha());
            redirectAttributes.addFlashAttribute("success", "Senha alterada com sucesso!");
            return "redirect:/conta/editar";
        } catch (Exception e) {
            log.error("Erro ao alterar senha do usuario {}", sessionCliente.getId(), e);
            redirectAttributes.addFlashAttribute("error", "Erro ao alterar senha. Tente novamente.");
            return "redirect:/conta/alterar-senha";
        }
    }

    @GetMapping("/minhaconta")
    public String accountRedirectLegacy() {
        return "redirect:/account";
    }
}
