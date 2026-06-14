package com.example.Millesime.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Millesime.model.Produto;
import com.example.Millesime.model.ProdutoService;

/**
 * Millésime - Home Controller
 * Controlador principal para renderizar os templates HTML/Thymeleaf
 */
@Controller
public class HomeController {

    private final ProdutoService produtoService;

    public HomeController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Página inicial
     * Exibe destaques, categorias e recomendações
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Millésime - Vinhos Premium");
        
        // Dados de exemplo para destaques
        model.addAttribute("featuredWines", new String[]{
            "Vinho Tinto Premium",
            "Vinho Branco Elegante",
            "Vinho Rosé Sofisticado",
            "Vinho Espumante Premium"
        });
        
        // Categorias
        model.addAttribute("categories", new String[]{
            "Tintos",
            "Brancos",
            "Rosés",
            "Espumantes"
        });
        
        return "index";
    }

    /**
     * Página de catálogo
     * Exibe lista de vinhos com filtros e paginação
     */
    @GetMapping("/catalogo")
    public String catalog(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String preco,
            @RequestParam(required = false) String ordem) throws Exception {
        final int pageSize = 12;

        int totalCount;
        List<Produto> produtos;

        if (type != null && !type.isBlank()) {
            totalCount = produtoService.contarPorTipo(type);
            produtos = produtoService.filtrarPorTipo(type, page, pageSize);
        } else {
            totalCount = produtoService.contarTodos();
            produtos = produtoService.listarTodos(page, pageSize);
        }

        int totalPages = Math.max(1, (totalCount + pageSize - 1) / pageSize);
        page = Math.min(Math.max(page, 1), totalPages);

        model.addAttribute("pageTitle", "Catálogo de Vinhos - Millésime");
        model.addAttribute("produtos", produtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedRegion", region);
        model.addAttribute("ordem", ordem);

        return "catalog";
    }

    /**
     * Página de detalhes do produto
     * Exibe informações completas do vinho selecionado
     */
    @GetMapping("/produto/{id}")
    public String product(
            @PathVariable UUID id,
            Model model) throws Exception {
        Produto produto = produtoService.buscarPorId(id);
        if (produto == null) {
            return "redirect:/catalogo";
        }

        model.addAttribute("pageTitle", produto.getNome() + " - Millésime");
        model.addAttribute("product", produto);
        return "product";
    }

    @GetMapping("/busca")
    public String search(Model model,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam String q) throws Exception {
        final int pageSize = 12;

        if (q == null || q.isBlank()) {
            return "redirect:/catalogo";
        }

        int totalCount = produtoService.contarPorNome(q);
        int totalPages = Math.max(1, (totalCount + pageSize - 1) / pageSize);
        page = Math.min(Math.max(page, 1), totalPages);

        List<Produto> produtos = produtoService.buscarPorNome(q, page, pageSize);

        model.addAttribute("pageTitle", "Busca: " + q + " - Millésime");
        model.addAttribute("produtos", produtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("termo", q);

        return "busca";
    }

    @GetMapping("/sobre")
    public String about(Model model) {
        model.addAttribute("pageTitle", "Sobre Nós - Millésime");
        
        return "about";
    }

    /**
     * Página de contato
     * Formulário de contato
     */
    @GetMapping("/contato")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contato - Millésime");
        
        return "contact";
    }

    @GetMapping("/politica-privacidade")
    public String privacyPolicy(Model model) {
        model.addAttribute("pageTitle", "Política de Privacidade - Millésime");
        return "politica-privacidade";
    }

    /**
     * Página 404 - Não encontrado
     */
    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("pageTitle", "Página não encontrada - Millésime");
        
        return "404";
    }
}
