package com.example.Millesime.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Millesime.model.Produto;
import com.example.Millesime.model.ProdutoService;

/**
 * REST Controller para gerenciar Produtos
 * Endpoints para CRUD completo de produtos
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoRestController {

    private final ProdutoService produtoService;

    public ProdutoRestController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * POST - Criar novo produto
     */
    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Produto produto) {
        try {
            produtoService.cadastrarProduto(produto);
            return ResponseEntity.status(201).body("Produto criado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET - Listar todos os produtos ativos
     */
    @GetMapping
    public ResponseEntity<List<Produto>> listar() throws Exception {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    /**
     * GET - Buscar produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable UUID id) throws Exception {
        Produto produto = produtoService.buscarPorId(id);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET - Filtrar produtos por tipo
     */
    @GetMapping("/filtro/tipo")
    public ResponseEntity<List<Produto>> filtrarPorTipo(@RequestParam String tipo) throws Exception {
        return ResponseEntity.ok(produtoService.filtrarPorTipo(tipo));
    }

    /**
     * PUT - Atualizar produto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable UUID id, @RequestBody Produto produto) {
        try {
            produto.setId(id);
            produtoService.atualizarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE - Deletar (desativar) produto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.ok("Produto deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
