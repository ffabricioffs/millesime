package com.example.Millesime.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Millesime.dto.ClienteSession;
import com.example.Millesime.model.ItemPedido;
import com.example.Millesime.model.Pedido;
import com.example.Millesime.model.PedidoService;
import com.example.Millesime.model.Produto;
import com.example.Millesime.model.ProdutoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public OrderController(ProdutoService produtoService, PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/carrinho")
    public String cart(HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Carrinho de Compras - Millésime");
        model.addAttribute("cartItems", getCartItems(session));
        model.addAttribute("subtotal", calcSubtotal(session));
        model.addAttribute("total", calcSubtotal(session));
        return "cart";
    }

    @PostMapping("/carrinho/adicionar")
    public String addToCart(@RequestParam UUID produtoId,
                            @RequestParam(defaultValue = "1") int quantidade,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            Produto produto = produtoService.buscarPorId(produtoId);
            if (produto == null || !produto.isAtivo()) {
                redirectAttributes.addFlashAttribute("cartError", "Produto não encontrado.");
                return "redirect:/catalogo";
            }

            @SuppressWarnings("unchecked")
            List<ItemPedido> carrinho = (List<ItemPedido>) session.getAttribute("carrinho");
            if (carrinho == null) {
                carrinho = new ArrayList<>();
                session.setAttribute("carrinho", carrinho);
            }

            boolean found = false;
            for (ItemPedido item : carrinho) {
                if (item.getProdutoId().equals(produtoId)) {
                    item.setQuantidade(item.getQuantidade() + quantidade);
                    found = true;
                    break;
                }
            }

            if (!found) {
                ItemPedido item = new ItemPedido();
                item.setProdutoId(produtoId);
                item.setNomeProduto(produto.getNome());
                item.setPrecoUnitario(produto.getPreco());
                item.setQuantidade(quantidade);
                carrinho.add(item);
            }

            redirectAttributes.addFlashAttribute("cartSuccess", "Produto adicionado ao carrinho.");
        } catch (Exception e) {
            log.error("Erro ao adicionar produto {} ao carrinho", produtoId, e);
            redirectAttributes.addFlashAttribute("cartError", "Erro ao adicionar produto.");
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/atualizar")
    public String updateCart(@RequestParam int index, @RequestParam int quantidade, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<ItemPedido> carrinho = (List<ItemPedido>) session.getAttribute("carrinho");
        if (carrinho != null && index >= 0 && index < carrinho.size() && quantidade > 0) {
            carrinho.get(index).setQuantidade(quantidade);
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removeFromCart(@RequestParam int index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<ItemPedido> carrinho = (List<ItemPedido>) session.getAttribute("carrinho");
        if (carrinho != null && index >= 0 && index < carrinho.size()) {
            carrinho.remove(index);
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/pedido/{id}/cancelar")
    public String cancelOrder(@PathVariable UUID id, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }

        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            if (!pedido.getClienteId().equals(sessionCliente.getId())) {
                redirectAttributes.addFlashAttribute("error", "Este pedido não pertence a você.");
                return "redirect:/meus-pedidos";
            }

            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("success", "Pedido cancelado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao cancelar pedido {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erro ao cancelar pedido.");
        }
        return "redirect:/pedido/" + id;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }

        List<ItemPedido> carrinho = getCartItems(session);
        if (carrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        model.addAttribute("pageTitle", "Finalizar Compra - Millésime");
        model.addAttribute("cartItems", carrinho);
        model.addAttribute("subtotal", calcSubtotal(session));
        model.addAttribute("total", calcSubtotal(session));
        model.addAttribute("cliente", sessionCliente);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return "checkout";
    }

    @PostMapping("/pedido/finalizar")
    public String placeOrder(HttpSession session,
                             RedirectAttributes redirectAttributes) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<ItemPedido> carrinho = (List<ItemPedido>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        try {
            List<ItemPedido> itens = new ArrayList<>(carrinho);
            Pedido pedido = pedidoService.criarPedido(sessionCliente.getId(), itens);
            session.removeAttribute("carrinho");
            return "redirect:/pedido/" + pedido.getId() + "?success";
        } catch (Exception e) {
            log.error("Erro ao finalizar pedido do usuario {}", sessionCliente.getId(), e);
            redirectAttributes.addFlashAttribute("error", "Erro ao finalizar pedido.");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/pedido/{id}")
    public String orderDetail(@PathVariable UUID id, Model model,
                              HttpSession session,
                              @RequestParam(required = false) String success) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }

        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            if (!pedido.getClienteId().equals(sessionCliente.getId())) {
                return "redirect:/meus-pedidos";
            }

            model.addAttribute("pageTitle", "Pedido #" + pedido.getId().toString().substring(0, 8) + " - Millésime");
            model.addAttribute("pedido", pedido);
            if (success != null) {
                model.addAttribute("success", "Pedido realizado com sucesso!");
            }
            return "pedido-detalhe";
        } catch (Exception e) {
            return "redirect:/meus-pedidos";
        }
    }

    @GetMapping("/meus-pedidos")
    public String myOrders(Model model, HttpSession session,
                           @RequestParam(defaultValue = "1") int page) {
        ClienteSession sessionCliente = (ClienteSession) session.getAttribute("clienteLogado");
        if (sessionCliente == null) {
            return "redirect:/login";
        }

        try {
            int pageSize = 10;
            int totalCount = pedidoService.contarPorCliente(sessionCliente.getId());
            int totalPages = Math.max(1, (totalCount + pageSize - 1) / pageSize);
            page = Math.min(Math.max(page, 1), totalPages);

            model.addAttribute("pageTitle", "Meus Pedidos - Millésime");
            model.addAttribute("pedidos", pedidoService.listarPorCliente(sessionCliente.getId(), page, pageSize));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            return "meus-pedidos";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @SuppressWarnings("unchecked")
    private List<ItemPedido> getCartItems(HttpSession session) {
        List<ItemPedido> carrinho = (List<ItemPedido>) session.getAttribute("carrinho");
        return carrinho != null ? carrinho : new ArrayList<>();
    }

    private double calcSubtotal(HttpSession session) {
        return getCartItems(session).stream()
            .mapToDouble(ItemPedido::getSubtotal)
            .sum();
    }
}
