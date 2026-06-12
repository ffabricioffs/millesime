package com.example.Millesime.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Millesime.exception.ValidationException;

@Service
@Transactional
public class ProdutoService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("Tinto", "Branco", "Rosé", "Rose", "Espumante");

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
        if (id == null) {
            throw new ValidationException("O id do produto é obrigatório.");
        }

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
        if (tipo == null || tipo.isBlank()) {
            throw new ValidationException("O tipo do produto é obrigatório para filtro.");
        }

        try {
            return produtoDAO.buscarPorTipo(tipo);
        } catch (SQLException e) {
            throw new Exception("Erro ao filtrar produtos por tipo.", e);
        }
    }

    public List<Produto> filtrarPorTipo(String tipo, int page, int pageSize) throws Exception {
        if (tipo == null || tipo.isBlank()) {
            throw new ValidationException("O tipo do produto é obrigatório para filtro.");
        }

        try {
            return produtoDAO.buscarPorTipo(tipo, page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Erro ao filtrar produtos por tipo.", e);
        }
    }

    public int contarPorTipo(String tipo) throws Exception {
        if (tipo == null || tipo.isBlank()) {
            throw new ValidationException("O tipo do produto é obrigatório para contagem.");
        }

        try {
            return produtoDAO.contarPorTipo(tipo);
        } catch (SQLException e) {
            throw new Exception("Erro ao contar produtos por tipo.", e);
        }
    }

    public void atualizarProduto(Produto produto) throws Exception {
        if (produto == null || produto.getId() == null) {
            throw new ValidationException("O id do produto é obrigatório.");
        }

        validarProduto(produto);

        try {
            produtoDAO.atualizar(produto);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar produto.", e);
        }
    }

    public void deletarProduto(UUID id) throws Exception {
        if (id == null) {
            throw new ValidationException("O id do produto é obrigatório.");
        }

        try {
            produtoDAO.deletar(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar produto.", e);
        }
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new ValidationException("Produto nao informado.");
        }

        if (produto.getNome() == null || produto.getNome().isBlank() || produto.getNome().trim().length() < 3) {
            throw new ValidationException("O nome do produto e obrigatorio e deve ter pelo menos 3 caracteres.");
        }

        if (produto.getDescricao() == null || produto.getDescricao().isBlank() || produto.getDescricao().trim().length() < 10) {
            throw new ValidationException("A descricao do produto e obrigatoria e deve ter pelo menos 10 caracteres.");
        }

        if (produto.getTipo() == null || produto.getTipo().isBlank() || !isTipoValido(produto.getTipo())) {
            throw new ValidationException("O tipo de produto informado e invalido. Tipos validos: Tinto, Branco, Rosé, Espumante.");
        }

        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new ValidationException("O preco deve ser maior que zero.");
        }

        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new ValidationException("O estoque nao pode ser negativo.");
        }
    }

    private boolean isTipoValido(String tipo) {
        return tipo != null && TIPOS_VALIDOS.stream()
                .anyMatch(valid -> valid.equalsIgnoreCase(tipo.trim()));
    }
}
