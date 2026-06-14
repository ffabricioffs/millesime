package com.example.Millesime.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ClienteService clienteService;

    public CustomAuthenticationSuccessHandler(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        try {
            Cliente cliente = clienteService.buscarPorEmail(email);
            if (cliente != null) {
                request.getSession().setAttribute("clienteLogado", cliente);
            }
        } catch (Exception e) {
            logger.warn("Could not load cliente for session", e);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
