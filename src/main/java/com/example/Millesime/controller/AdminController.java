package com.example.Millesime.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;
import com.example.Millesime.model.Pedido;
import com.example.Millesime.model.PedidoService;
import com.example.Millesime.model.Produto;
import com.example.Millesime.model.ProdutoService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public AdminController(ProdutoService produtoService,
                           PedidoService pedidoService,
                           ClienteService clienteService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String dashboard(Model model) throws Exception {
        model.addAttribute("totalProdutos", produtoService.contarTodos());
        model.addAttribute("totalPedidos", pedidoService.contarTodos());
        model.addAttribute("totalClientes", clienteService.listarTodosClientes().size());

        List<Pedido> ultimosPedidos = pedidoService.listarTodos(1, 5);
        for (Pedido p : ultimosPedidos) {
            try {
                Cliente c = clienteService.buscarPorId(p.getClienteId());
                p.setClienteNome(c.getNomeCompleto());
            } catch (Exception e) {
                log.error("Erro ao buscar cliente do pedido {}", p.getId(), e);
                p.setClienteNome("---");
            }
        }
        model.addAttribute("ultimosPedidos", ultimosPedidos);
        model.addAttribute("pageTitle", "Painel Administrativo - Mill\u00e9sime");
        return "admin/index";
    }

    @GetMapping("/produtos")
    public String listarProdutos(Model model,
                                  @RequestParam(defaultValue = "1") int page) throws Exception {
        int total = produtoService.contarTodosAdmin();
        int pageSize = 15;
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.min(Math.max(page, 1), totalPages);

        model.addAttribute("produtos", produtoService.listarTodosAdmin(page, pageSize));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageTitle", "Produtos - Admin");
        return "admin/produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("pageTitle", "Novo Produto - Admin");
        return "admin/produtos-form";
    }

    @PostMapping("/produtos")
    public String salvarProduto(@Valid @ModelAttribute Produto produto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Produto - Admin");
            return "admin/produtos-form";
        }
        try {
            if (produto.getEstoque() == null) produto.setEstoque(0);
            produto.setAtivo(true);
            produtoService.cadastrarProduto(produto);
            redirectAttributes.addFlashAttribute("success", "Produto criado com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao salvar produto", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar produto.");
        }
        return "redirect:/admin/produtos";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable UUID id, Model model) {
        try {
            model.addAttribute("produto", produtoService.buscarPorId(id));
            model.addAttribute("pageTitle", "Editar Produto - Admin");
            return "admin/produtos-form";
        } catch (Exception e) {
            log.error("Erro ao carregar produto {} para edicao", id, e);
            return "redirect:/admin/produtos";
        }
    }

    @PostMapping("/produtos/{id}")
    public String atualizarProduto(@PathVariable UUID id,
                                    @Valid @ModelAttribute Produto produto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Produto - Admin");
            return "admin/produtos-form";
        }
        try {
            produto.setId(id);
            if (produto.getEstoque() == null) produto.setEstoque(0);
            produtoService.atualizarProduto(produto);
            redirectAttributes.addFlashAttribute("success", "Produto atualizado!");
        } catch (Exception e) {
            log.error("Erro ao atualizar produto", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar produto.");
        }
        return "redirect:/admin/produtos";
    }

    @PostMapping("/produtos/{id}/deletar")
    public String deletarProduto(@PathVariable UUID id,
                                  RedirectAttributes redirectAttributes) {
        try {
            produtoService.deletarProduto(id);
            redirectAttributes.addFlashAttribute("success", "Produto desativado.");
        } catch (Exception e) {
            log.error("Erro ao desativar produto", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao desativar produto.");
        }
        return "redirect:/admin/produtos";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(Model model,
                                 @RequestParam(defaultValue = "1") int page) throws Exception {
        int total = pedidoService.contarTodos();
        int pageSize = 15;
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.min(Math.max(page, 1), totalPages);

        List<Pedido> pedidos = pedidoService.listarTodos(page, pageSize);
        for (Pedido p : pedidos) {
            try {
                Cliente c = clienteService.buscarPorId(p.getClienteId());
                p.setClienteNome(c.getNomeCompleto());
            } catch (Exception e) {
                log.error("Erro ao buscar cliente do pedido {}", p.getId(), e);
                p.setClienteNome("---");
            }
        }
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageTitle", "Pedidos - Admin");
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/status")
    public String atualizarStatus(@PathVariable UUID id,
                                    @RequestParam(required = false) String status,
                                   RedirectAttributes redirectAttributes) {
        if (status == null || status.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Status \u00e9 obrigat\u00f3rio.");
            return "redirect:/admin/pedidos";
        }
        // manter sincronizado com PedidoService.STATUS_VALIDOS
        Set<String> validos = Set.of("PENDENTE", "CONFIRMADO", "ENVIADO", "ENTREGUE", "CANCELADO");
        if (!validos.contains(status.toUpperCase())) {
            redirectAttributes.addFlashAttribute("error", "Status inv\u00e1lido: " + status);
            return "redirect:/admin/pedidos";
        }
        try {
            pedidoService.atualizarStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Status atualizado para " + status + ".");
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar status do pedido.");
        }
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        try {
            model.addAttribute("clientes", clienteService.listarTodosClientes());
            model.addAttribute("pageTitle", "Clientes - Admin");
        } catch (Exception e) {
            log.error("Erro ao carregar lista de clientes", e);
            model.addAttribute("clientes", List.of());
            model.addAttribute("error", "Erro ao carregar clientes.");
        }
        return "admin/clientes";
    }
}
