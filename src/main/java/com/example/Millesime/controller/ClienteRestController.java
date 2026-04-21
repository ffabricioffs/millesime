package com.example.Millesime.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<Cliente>> listar() throws Exception {
        return ResponseEntity.ok(clienteService.listarTodosClientes());
    }

    /**
     * GET - Buscar cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable UUID id) throws Exception {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            if (cliente != null && cliente.isAtivo()) {
                return ResponseEntity.ok(cliente);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * PUT - Atualizar cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable UUID id, @RequestBody Cliente cliente) {
        try {
            cliente.setId(id);
            clienteService.atualizarCadastro(cliente);
            return ResponseEntity.ok("Cliente atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
