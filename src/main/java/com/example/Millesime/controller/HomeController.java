package com.example.Millesime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

/**
 * Millésime - Home Controller
 * Controlador principal para renderizar os templates HTML/Thymeleaf
 */
@Controller
public class HomeController {

    private final ClienteService clienteService;

    public HomeController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Página inicial
     * Exibe destaques, categorias e recomendações
     */
    @GetMapping("/")
    public String index(Model model) {
        // Adicionar dados ao modelo para o template
        model.addAttribute("cartCount", 3);
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
            @RequestParam(required = false) String region) {
        
        model.addAttribute("pageTitle", "Catálogo de Vinhos - Millésime");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 5);
        model.addAttribute("totalCount", 48);
        
        // Filtros aplicados
        if (type != null) {
            model.addAttribute("selectedType", type);
        }
        if (region != null) {
            model.addAttribute("selectedRegion", region);
        }
        
        return "catalog";
    }

    /**
     * Página de detalhes do produto
     * Exibe informações completas do vinho selecionado
     */
    @GetMapping("/produto/{id}")
    public String product(
            @PathVariable Long id,
            Model model) {
        
        // Dados de exemplo do produto
        model.addAttribute("pageTitle", "Vinho Premium - Millésime");
        model.addAttribute("product", new Object() {
            public Long getId() { return id; }
            public String getName() { return "Vinho Tinto Premium"; }
            public String getRegion() { return "Napa Valley"; }
            public String getCountry() { return "EUA"; }
            public String getType() { return "Tinto"; }
            public String getGrape() { return "Cabernet Sauvignon"; }
            public Integer getHarvest() { return 2018; }
            public Double getPrice() { return 189.90; }
            public Integer getStock() { return 15; }
            public Double getRating() { return 4.8; }
            public Integer getReviews() { return 128; }
            public String getDescription() { 
                return "Este vinho tinto premium é uma obra-prima de elegância e sofisticação."; 
            }
        });
        
        return "product";
    }

    /**
     * Página de carrinho
     * Exibe itens adicionados ao carrinho
     */
    @GetMapping("/carrinho")
    public String cart(Model model) {
        model.addAttribute("pageTitle", "Carrinho de Compras - Millésime");
        model.addAttribute("cartItems", 3);
        model.addAttribute("subtotal", 569.70);
        model.addAttribute("tax", 85.45);
        model.addAttribute("total", 655.15);
        
        return "cart";
    }

    /**
     * Página de checkout
     * Finalização da compra
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("pageTitle", "Checkout - Millésime");
        model.addAttribute("total", 655.15);
        
        return "checkout";
    }

    /**
     * Página sobre
     * Informações sobre a loja
     */
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

    /**
     * Página de cadastro
     * Formulário para criação de conta
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Cadastro - Millésime");
        model.addAttribute("cliente", new Cliente());

        return "register";
    }

    /**
     * Processamento do cadastro
     * Recebe os dados do formulário, valida e salva o cliente
     */
    @PostMapping("/register")
    public String registerPost(Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            clienteService.cadastrarCliente(cliente);
            redirectAttributes.addFlashAttribute("email", cliente.getEmail());
            return "redirect:/register-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Página de sucesso após cadastro
     * Exibe instruções de verificação de e-mail
     */
    @GetMapping("/register-success")
    public String registerSuccess(Model model) {
        if (!model.containsAttribute("email")) {
            return "redirect:/register";
        }

        model.addAttribute("pageTitle", "Cadastro realizado - Millésime");

        return "register-success";
    }

    /**
     * Página de login
     * Formulário para acesso à conta
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Entrar - Millésime");
        
        return "login";
    }

    /**
     * Compatibilidade com link antigo de conta
     */
    @GetMapping("/conta")
    public String accountRedirect() {
        return "redirect:/login";
    }

    /**
     * Página de acesso com abas para login e cadastro
     */
    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("pageTitle", "Acesso - Millésime");
        
        return "account";
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
