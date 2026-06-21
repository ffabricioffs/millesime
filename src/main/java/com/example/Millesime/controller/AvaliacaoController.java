package com.example.Millesime.controller;

import com.example.Millesime.dto.AvaliacaoResponse;
import com.example.Millesime.dto.ClienteSession;
import com.example.Millesime.exception.ValidationException;
import com.example.Millesime.model.AvaliacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AvaliacaoController {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoController.class);
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping("/api/avaliacoes")
    public Object criar(@RequestParam UUID produtoId,
                        @RequestParam(defaultValue = "0") int nota,
                        @RequestParam(required = false) String comentario,
                        HttpSession session,
                        HttpServletRequest request,
                        RedirectAttributes redirect) {
        ClienteSession cliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (cliente == null) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Faça login para avaliar."));
            }
            return "redirect:/login?redirect=/produto/" + produtoId;
        }

        try {
            avaliacaoService.criar(produtoId, cliente.getId(), cliente.getNomeCompleto(), nota, comentario);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Avaliação enviada!"));
            }
            redirect.addFlashAttribute("avaliacaoSuccess", "Avaliação enviada com sucesso!");
        } catch (ValidationException e) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
            }
            redirect.addFlashAttribute("avaliacaoError", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao salvar avaliacao", e);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.status(500).body(Map.of("success", false, "message", "Erro interno."));
            }
            redirect.addFlashAttribute("avaliacaoError", "Erro ao enviar avaliação.");
        }
        return "redirect:/produto/" + produtoId;
    }

    @GetMapping("/api/avaliacoes/produto/{produtoId}")
    @ResponseBody
    public ResponseEntity<List<AvaliacaoResponse>> listar(@PathVariable UUID produtoId) {
        try {
            return ResponseEntity.ok(avaliacaoService.listarPorProduto(produtoId));
        } catch (Exception e) {
            log.error("Erro ao listar avaliacoes", e);
            return ResponseEntity.ok(List.of());
        }
    }
}
