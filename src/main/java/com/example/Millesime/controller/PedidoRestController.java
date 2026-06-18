package com.example.Millesime.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Millesime.dto.ItemPedidoResponse;
import com.example.Millesime.dto.PedidoResponse;
import com.example.Millesime.model.ItemPedido;
import com.example.Millesime.model.Pedido;
import com.example.Millesime.model.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private static final Logger log = LoggerFactory.getLogger(PedidoRestController.class);

    private static final Set<String> STATUS_VALIDOS = Set.of("PENDENTE", "CONFIRMADO", "ENVIADO", "ENTREGUE", "CANCELADO");

    private final PedidoService pedidoService;

    public PedidoRestController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) throws Exception {
        List<PedidoResponse> pedidos = pedidoService.listarTodos(page, size).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable UUID id) throws Exception {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(pedido));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> listarPorCliente(
            @PathVariable UUID clienteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) throws Exception {
        List<PedidoResponse> pedidos = pedidoService.listarPorCliente(clienteId, page, size).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> atualizarStatus(@PathVariable UUID id,
                                                   @RequestParam(required = false) String status) {
        if (status == null || status.isBlank() || !STATUS_VALIDOS.contains(status)) {
            return ResponseEntity.badRequest().body("Status inválido.");
        }
        try {
            pedidoService.atualizarStatus(id, status);
            return ResponseEntity.ok("Status atualizado.");
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido {} para {}", id, status, e);
            return ResponseEntity.badRequest().body("Erro ao atualizar status do pedido.");
        }
    }
    private PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setClienteId(pedido.getClienteId());
        response.setDataPedido(pedido.getDataPedido());
        response.setTotal(pedido.getTotal());
        response.setStatus(pedido.getStatus());
        if (pedido.getItens() != null) {
            response.setItens(pedido.getItens().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private ItemPedidoResponse toItemResponse(ItemPedido item) {
        ItemPedidoResponse resp = new ItemPedidoResponse();
        resp.setProdutoId(item.getProdutoId());
        resp.setNomeProduto(item.getNomeProduto());
        resp.setQuantidade(item.getQuantidade());
        resp.setPrecoUnitario(item.getPrecoUnitario());
        resp.setSubtotal(item.getSubtotal());
        return resp;
    }
}
