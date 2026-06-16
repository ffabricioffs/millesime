package com.example.Millesime.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Millesime.dto.ClienteRequest;
import com.example.Millesime.dto.ClienteResponse;
import com.example.Millesime.dto.ClienteSummaryResponse;
import com.example.Millesime.model.Cliente;
import com.example.Millesime.model.ClienteService;

/**
 * REST Controller para gerenciar Clientes
 * Endpoints para CRUD completo de clientes
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;

    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * GET - Listar todos os clientes ativos
     */
    @GetMapping
    public ResponseEntity<List<ClienteSummaryResponse>> listar() throws Exception {
        List<ClienteSummaryResponse> clientes = clienteService.listarTodosClientes().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    /**
     * GET - Buscar cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) throws Exception {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente != null && cliente.isAtivo()) {
            return ResponseEntity.ok(toResponse(cliente));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST - Criar novo cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) throws Exception {
        Cliente cliente = fromRequest(request);
        clienteService.cadastrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cliente));
    }

    /**
     * PUT - Atualizar cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable UUID id, @Valid @RequestBody ClienteRequest request) {
        try {
            Cliente cliente = fromRequest(request);
            cliente.setId(id);
            clienteService.atualizarCadastro(cliente);
            return ResponseEntity.ok("Cliente atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar cliente.");
        }
    }

    /**
     * DELETE - Deletar (desativar) cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        try {
            clienteService.desativarConta(id);
            return ResponseEntity.ok("Cliente deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar cliente.");
        }
    }

    private ClienteSummaryResponse toSummary(Cliente cliente) {
        ClienteSummaryResponse r = new ClienteSummaryResponse();
        r.setId(cliente.getId());
        r.setNomeCompleto(cliente.getNomeCompleto());
        r.setEmail(cliente.getEmail());
        r.setTelefone(cliente.getTelefone());
        r.setNewsletter(cliente.isNewsletter());
        r.setDataCadastro(cliente.getDataCadastro());
        r.setAtivo(cliente.isAtivo());
        return r;
    }

    private ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNomeCompleto(cliente.getNomeCompleto());
        response.setEmail(cliente.getEmail());
        response.setCpf(cliente.getCpf());
        response.setDataNascimento(cliente.getDataNascimento());
        response.setTelefone(cliente.getTelefone());
        response.setNewsletter(cliente.isNewsletter());
        response.setDataCadastro(cliente.getDataCadastro());
        response.setAtivo(cliente.isAtivo());
        return response;
    }

    private Cliente fromRequest(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(request.getNomeCompleto());
        cliente.setEmail(request.getEmail());
        cliente.setSenha(request.getSenha());
        cliente.setCpf(request.getCpf());
        cliente.setDataNascimento(request.getDataNascimento());
        cliente.setTelefone(request.getTelefone());
        cliente.setNewsletter(request.isNewsletter());
        return cliente;
    }
}
