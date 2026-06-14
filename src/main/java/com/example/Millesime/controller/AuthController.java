package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

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
    public String accountRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/minhaconta")
    public String accountRedirectLegacy() {
        return "redirect:/account";
    }
}
