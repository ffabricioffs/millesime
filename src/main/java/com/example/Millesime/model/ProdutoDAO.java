package com.example.Millesime.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

@Repository
public class ProdutoDAO {

    private static final String INSERT_SQL = """
            INSERT INTO produto (id, nome, descricao, tipo, regiao, pais, uva, preco, estoque, data_criacao, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM produto WHERE id = ?";
    private static final String SELECT_ALL_ACTIVE_SQL = "SELECT * FROM produto WHERE ativo = true ORDER BY nome";
    private static final String SELECT_BY_TIPO_SQL = "SELECT * FROM produto WHERE tipo = ? AND ativo = true";
    private static final String UPDATE_SQL = """
            UPDATE produto
            SET nome = ?, descricao = ?, tipo = ?, regiao = ?, pais = ?, uva = ?, preco = ?, estoque = ?, ativo = ?
            WHERE id = ?
            """;
    private static final String SOFT_DELETE_SQL = "UPDATE produto SET ativo = false WHERE id = ?";

    private final DataSource dataSource;

    public ProdutoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(Produto produto) throws SQLException {
        if (produto.getId() == null) {
            produto.setId(UUID.randomUUID());
        }
        if (produto.getDataCriacao() == null) {
            produto.setDataCriacao(LocalDateTime.now());
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, produto.getId());
            statement.setString(2, produto.getNome());
            statement.setString(3, produto.getDescricao());
            statement.setString(4, produto.getTipo());
            statement.setString(5, produto.getRegiao());
            statement.setString(6, produto.getPais());
            statement.setString(7, produto.getUva());
            statement.setDouble(8, produto.getPreco());
            statement.setInt(9, produto.getEstoque());
            statement.setTimestamp(10, Timestamp.valueOf(produto.getDataCriacao()));
            statement.setBoolean(11, produto.isAtivo());
            statement.executeUpdate();
        }
    }

    public Produto buscarPorId(UUID id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setObject(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearProduto(resultSet);
                }
            }
        }

        return null;
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ACTIVE_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                produtos.add(mapearProduto(resultSet));
            }
        }

        return produtos;
    }

    public List<Produto> buscarPorTipo(String tipo) throws SQLException {
        List<Produto> produtos = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_TIPO_SQL)) {
            statement.setString(1, tipo);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    produtos.add(mapearProduto(resultSet));
                }
            }
        }

        return produtos;
    }

    public void atualizar(Produto produto) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, produto.getNome());
            statement.setString(2, produto.getDescricao());
            statement.setString(3, produto.getTipo());
            statement.setString(4, produto.getRegiao());
            statement.setString(5, produto.getPais());
            statement.setString(6, produto.getUva());
            statement.setDouble(7, produto.getPreco());
            statement.setInt(8, produto.getEstoque());
            statement.setBoolean(9, produto.isAtivo());
            statement.setObject(10, produto.getId());
            statement.executeUpdate();
        }
    }

    public void deletar(UUID id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SOFT_DELETE_SQL)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        }
    }

    private Produto mapearProduto(ResultSet resultSet) throws SQLException {
        Produto produto = new Produto();
        produto.setId((UUID) resultSet.getObject("id"));
        produto.setNome(resultSet.getString("nome"));
        produto.setDescricao(resultSet.getString("descricao"));
        produto.setTipo(resultSet.getString("tipo"));
        produto.setRegiao(resultSet.getString("regiao"));
        produto.setPais(resultSet.getString("pais"));
        produto.setUva(resultSet.getString("uva"));
        produto.setPreco(resultSet.getDouble("preco"));
        produto.setEstoque(resultSet.getInt("estoque"));
        produto.setDataCriacao(resultSet.getTimestamp("data_criacao").toLocalDateTime());
        produto.setAtivo(resultSet.getBoolean("ativo"));
        return produto;
    }
}
