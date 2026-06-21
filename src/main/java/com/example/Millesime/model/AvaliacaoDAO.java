package com.example.Millesime.model;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AvaliacaoDAO {

    private static final String INSERT_SQL = "INSERT INTO avaliacao (id, produto_id, cliente_id, cliente_nome, nota, comentario, data) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_PRODUTO_SQL = "SELECT * FROM avaliacao WHERE produto_id = ? ORDER BY data DESC";
    private static final String COUNT_BY_PRODUTO_SQL = "SELECT COUNT(*) FROM avaliacao WHERE produto_id = ?";
    private static final String AVG_BY_PRODUTO_SQL = "SELECT COALESCE(AVG(nota), 0) FROM avaliacao WHERE produto_id = ?";
    private static final String COUNT_BY_PRODUTO_CLIENTE_SQL = "SELECT COUNT(*) FROM avaliacao WHERE produto_id = ? AND cliente_id = ?";

    private final DataSource dataSource;

    public AvaliacaoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(Avaliacao avaliacao) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setObject(1, avaliacao.getId());
            stmt.setObject(2, avaliacao.getProdutoId());
            stmt.setObject(3, avaliacao.getClienteId());
            stmt.setString(4, truncate(avaliacao.getClienteNome(), 100));
            stmt.setInt(5, avaliacao.getNota());
            stmt.setString(6, truncate(avaliacao.getComentario(), 1000));
            stmt.setTimestamp(7, Timestamp.valueOf(avaliacao.getData()));
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<Avaliacao> listarPorProduto(UUID produtoId) throws SQLException {
        List<Avaliacao> lista = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_PRODUTO_SQL)) {
            stmt.setObject(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAvaliacao(rs));
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return lista;
    }

    public int contarPorProduto(UUID produtoId) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_BY_PRODUTO_SQL)) {
            stmt.setObject(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    public double mediaPorProduto(UUID produtoId) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(AVG_BY_PRODUTO_SQL)) {
            stmt.setObject(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0.0;
    }

    public int contarPorProdutoECliente(UUID produtoId, UUID clienteId) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_BY_PRODUTO_CLIENTE_SQL)) {
            stmt.setObject(1, produtoId);
            stmt.setObject(2, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    private Avaliacao mapearAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao a = new Avaliacao();
        a.setId(rs.getObject("id", UUID.class));
        a.setProdutoId(rs.getObject("produto_id", UUID.class));
        a.setClienteId(rs.getObject("cliente_id", UUID.class));
        a.setClienteNome(rs.getString("cliente_nome"));
        a.setNota(rs.getInt("nota"));
        a.setComentario(rs.getString("comentario"));
        Timestamp ts = rs.getTimestamp("data");
        if (ts != null) a.setData(ts.toLocalDateTime());
        return a;
    }

    public List<Avaliacao> listarPorProdutoPaginado(UUID produtoId, int page, int size) throws SQLException {
        List<Avaliacao> lista = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM avaliacao WHERE produto_id = ? ORDER BY data DESC LIMIT ? OFFSET ?")) {
            stmt.setObject(1, produtoId);
            stmt.setInt(2, size);
            stmt.setInt(3, (page - 1) * size);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAvaliacao(rs));
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return lista;
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}
