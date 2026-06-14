package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.dto.ClienteRegisterRequest;
import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

@Controller
public class RegisterController {

    private final ClienteService clienteService;

    public RegisterController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Cadastro - Millésime");
        model.addAttribute("clienteRegisterRequest", new ClienteRegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute ClienteRegisterRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNomeCompleto(request.getNomeCompleto());
            cliente.setEmail(request.getEmail());
            cliente.setSenha(request.getSenha());
            cliente.setCpf(request.getCpf());
            cliente.setDataNascimento(request.getDataNascimento());
            cliente.setTelefone(request.getTelefone());
            cliente.setNewsletter(request.isNewsletter());

            clienteService.cadastrarCliente(cliente);
            redirectAttributes.addFlashAttribute("email", cliente.getEmail());
            return "redirect:/register-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/register-success")
    public String registerSuccess(Model model) {
        if (!model.containsAttribute("email")) {
            return "redirect:/register";
        }
        model.addAttribute("pageTitle", "Cadastro realizado - Millésime");
        return "register-success";
    }
}
