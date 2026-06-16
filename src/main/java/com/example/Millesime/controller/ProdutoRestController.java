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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Millesime.dto.ProdutoRequest;
import com.example.Millesime.dto.ProdutoResponse;
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
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) throws Exception {
        Produto produto = fromRequest(request);
        produtoService.cadastrarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(produto));
    }

    /**
     * GET - Listar todos os produtos ativos
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() throws Exception {
        List<ProdutoResponse> produtos = produtoService.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    /**
     * GET - Buscar produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable UUID id) throws Exception {
        Produto produto = produtoService.buscarPorId(id);
        if (produto != null) {
            return ResponseEntity.ok(toResponse(produto));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET - Filtrar produtos por tipo
     */
    @GetMapping("/filtro/tipo")
    public ResponseEntity<List<ProdutoResponse>> filtrarPorTipo(@RequestParam(required = false) String tipo) throws Exception {
        List<ProdutoResponse> produtos = produtoService.filtrarPorTipo(tipo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    /**
     * PUT - Atualizar produto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoRequest request) {
        try {
            Produto produto = fromRequest(request);
            produto.setId(id);
            produtoService.atualizarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar produto.");
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
            return ResponseEntity.badRequest().body("Erro ao deletar produto.");
        }
    }

    private ProdutoResponse toResponse(Produto produto) {
        ProdutoResponse response = new ProdutoResponse();
        response.setId(produto.getId());
        response.setNome(produto.getNome());
        response.setDescricao(produto.getDescricao());
        response.setTipo(produto.getTipo());
        response.setRegiao(produto.getRegiao());
        response.setPais(produto.getPais());
        response.setUva(produto.getUva());
        response.setPreco(produto.getPreco());
        response.setEstoque(produto.getEstoque());
        response.setImagem(produto.getImagem());
        response.setDataCriacao(produto.getDataCriacao());
        response.setAtivo(produto.isAtivo());
        return response;
    }

    private Produto fromRequest(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setTipo(request.getTipo());
        produto.setRegiao(request.getRegiao());
        produto.setPais(request.getPais());
        produto.setUva(request.getUva());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        produto.setImagem(request.getImagem());
        return produto;
    }
}
