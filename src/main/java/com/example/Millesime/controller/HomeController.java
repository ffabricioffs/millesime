package com.example.Millesime.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;
import com.example.Millesime.model.Produto;
import com.example.Millesime.model.ProdutoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Millésime - Home Controller
 * Controlador principal para renderizar os templates HTML/Thymeleaf
 */
@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public HomeController(ClienteService clienteService, ProdutoService produtoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
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
            @RequestParam(required = false) String region) throws Exception {
        final int pageSize = 12;

        int totalCount;

        if (type != null && !type.isBlank()) {
            totalCount = produtoService.contarPorTipo(type);
        } else {
            totalCount = produtoService.contarTodos();
        }

        int totalPages = Math.max(1, (totalCount + pageSize - 1) / pageSize);
        page = Math.min(Math.max(page, 1), totalPages);

        List<Produto> produtos;
        if (type != null && !type.isBlank()) {
            produtos = produtoService.filtrarPorTipo(type, page, pageSize);
        } else {
            produtos = produtoService.listarTodos(page, pageSize);
        }

        model.addAttribute("pageTitle", "Catálogo de Vinhos - Millésime");
        model.addAttribute("produtos", produtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedRegion", region);

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

    @PostMapping("/enviar-contato")
    public String enviarContato(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String mensagem,
            @RequestParam(required = false) String newsletter,
            @RequestParam(required = false) String privacidade,
            RedirectAttributes redirectAttributes) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank() || assunto == null || assunto.isBlank() || mensagem == null || mensagem.isBlank() || privacidade == null) {
            redirectAttributes.addFlashAttribute("contactError", "Preencha os campos obrigatórios e concorde com a Política de Privacidade.");
            return "redirect:/contato";
        }

        redirectAttributes.addFlashAttribute("contactSuccess", "Mensagem enviada com sucesso. Em breve retornaremos! Obrigado pelo contato.");
        return "redirect:/contato";
    }

    @GetMapping("/politica-privacidade")
    public String privacyPolicy(Model model) {
        model.addAttribute("pageTitle", "Política de Privacidade - Millésime");
        return "politica-privacidade";
    }

    @GetMapping("/faleconosco")
    public String contactRedirect() {
        return "redirect:/contato";
    }

    @GetMapping("/minhaconta")
    public String accountRedirectLegacy() {
        return "redirect:/account";
    }

    @PostMapping("/newsletter")
    public String newsletter(@RequestParam String email, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("newsletterError", "Digite um e-mail válido para receber nossas novidades.");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("newsletterSuccess", "Obrigado! Você foi inscrito para receber nossas novidades.");
        return "redirect:/";
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

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("pageTitle", "Recuperar Senha - Millésime");
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String email,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Informe um e-mail válido para recuperar a senha.");
            return "redirect:/reset-password";
        }

        String baseUrl = String.format("%s://%s%s", request.getScheme(), request.getServerName(), request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
        try {
            clienteService.criarTokenRedefinicaoSenha(email, baseUrl);
            redirectAttributes.addFlashAttribute("message", "Se o e-mail estiver cadastrado, você receberá instruções de recuperação em breve.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Não foi possível processar sua solicitação no momento. Tente novamente mais tarde.");
        }
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password/confirm")
    public String confirmResetPassword(@RequestParam String token, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.validarTokenRedefinicaoSenha(token);
            model.addAttribute("pageTitle", "Redefinir Senha - Millésime");
            model.addAttribute("token", token);
            model.addAttribute("cliente", cliente);
            return "reset-password-confirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password";
        }
    }

    @PostMapping("/reset-password/confirm")
    public String confirmResetPasswordSubmit(@RequestParam String token,
                                              @RequestParam String novaSenha,
                                              RedirectAttributes redirectAttributes) {
        try {
            clienteService.redefinirSenha(token, novaSenha);
            redirectAttributes.addFlashAttribute("message", "Senha redefinida com sucesso. Faça login com sua nova senha.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password/confirm?token=" + token;
        }
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        try {
            Cliente cliente = clienteService.autenticar(email, password);
            session.setAttribute("clienteLogado", cliente);
            return "redirect:/account";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loginError", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutSuccess", "Logout realizado com sucesso.");
        return "redirect:/";
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
