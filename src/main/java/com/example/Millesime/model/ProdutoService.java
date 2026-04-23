package com.example.Millesime.model;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void cadastrarProduto(Produto produto) throws Exception {
        validarProduto(produto);

        try {
            produtoDAO.salvar(produto);
        } catch (SQLException e) {
            throw new Exception("Erro ao cadastrar produto.", e);
        }
    }

    public Produto buscarPorId(UUID id) throws Exception {
        try {
            return produtoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar produto.", e);
        }
    }

    public List<Produto> listarTodos() throws Exception {
        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar produtos.", e);
        }
    }

    public List<Produto> listarTodos(int page, int pageSize) throws Exception {
        try {
            return produtoDAO.listarTodos(page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Erro ao listar produtos.", e);
        }
    }

    public int contarTodos() throws Exception {
        try {
            return produtoDAO.contarTodos();
        } catch (SQLException e) {
            throw new Exception("Erro ao contar produtos.", e);
        }
    }

    public List<Produto> filtrarPorTipo(String tipo) throws Exception {
        try {
            return produtoDAO.buscarPorTipo(tipo);
        } catch (SQLException e) {
            throw new Exception("Erro ao filtrar produtos por tipo.", e);
        }
    }

    public List<Produto> filtrarPorTipo(String tipo, int page, int pageSize) throws Exception {
        try {
            return produtoDAO.buscarPorTipo(tipo, page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Erro ao filtrar produtos por tipo.", e);
        }
    }

    public int contarPorTipo(String tipo) throws Exception {
        try {
            return produtoDAO.contarPorTipo(tipo);
        } catch (SQLException e) {
            throw new Exception("Erro ao contar produtos por tipo.", e);
        }
    }

    public void atualizarProduto(Produto produto) throws Exception {
        if (produto.getId() == null) {
            throw new Exception("O id do produto é obrigatório.");
        }

        validarProduto(produto);

        try {
            produtoDAO.atualizar(produto);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar produto.", e);
        }
    }

    public void deletarProduto(UUID id) throws Exception {
        try {
            produtoDAO.deletar(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar produto.", e);
        }
    }

    private void validarProduto(Produto produto) throws Exception {
        if (produto == null) {
            throw new Exception("Produto não informado.");
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new Exception("O nome do produto é obrigatório.");
        }

        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new Exception("O preço deve ser maior que zero.");
        }

        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new Exception("O estoque não pode ser negativo.");
        }
    }
}
