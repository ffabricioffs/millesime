package com.example.Millesime.model;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.datasource.DataSourceUtils;

@Repository
public class ProdutoDAO {

    private static final String INSERT_SQL = """
            INSERT INTO produto (
                id, nome, descricao, tipo, uva, pais, regiao, preco, estoque, imagem, data_criacao, ativo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String COLUNAS = "id, nome, descricao, tipo, uva, pais, regiao, preco, estoque, imagem, data_criacao, ativo";
    private static final String SELECT_BY_ID_SQL = "SELECT " + COLUNAS + " FROM produto WHERE id = ?";
    private static final String SELECT_ALL_ATIVOS_SQL = "SELECT " + COLUNAS + " FROM produto WHERE ativo = true ORDER BY nome";
    private static final String SELECT_ALL_ADMIN_SQL = "SELECT " + COLUNAS + " FROM produto ORDER BY nome";
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM produto";
    private static final String COUNT_ATIVOS_SQL = "SELECT COUNT(*) FROM produto WHERE ativo = true";
    private static final String UPDATE_SQL = """
            UPDATE produto SET nome = ?, descricao = ?, tipo = ?, uva = ?, pais = ?, regiao = ?,
                preco = ?, estoque = ?, imagem = ?, ativo = ? WHERE id = ?
            """;
    private static final String SOFT_DELETE_SQL = "UPDATE produto SET ativo = false WHERE id = ?";
    private static final String BAIXAR_ESTOQUE_SQL = "UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";
    private static final String SELECT_DESTAQUES_SQL = "SELECT " + COLUNAS + " FROM produto WHERE ativo = true ORDER BY nome LIMIT 8";
    private static final String SELECT_BY_TIPO_SQL = "SELECT " + COLUNAS + " FROM produto WHERE ativo = true AND tipo = ? ORDER BY nome";
    private static final String SEARCH_SQL = "SELECT " + COLUNAS + " FROM produto WHERE ativo = true AND (LOWER(nome) LIKE ? OR LOWER(tipo) LIKE ? OR LOWER(uva) LIKE ? OR LOWER(pais) LIKE ? OR LOWER(regiao) LIKE ?) ORDER BY nome";

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
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setObject(1, produto.getId());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getDescricao());
            stmt.setString(4, produto.getTipo());
            stmt.setString(5, produto.getUva());
            stmt.setString(6, produto.getPais());
            stmt.setString(7, produto.getRegiao());
            stmt.setDouble(8, produto.getPreco());
            stmt.setInt(9, produto.getEstoque());
            stmt.setString(10, produto.getImagem());
            stmt.setTimestamp(11, Timestamp.valueOf(produto.getDataCriacao()));
            stmt.setBoolean(12, produto.isAtivo());
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Produto buscarPorId(UUID id) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProduto(rs);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return null;
    }

    public List<Produto> listarTodos() throws SQLException {
        return listar(SELECT_ALL_ATIVOS_SQL, null, 0, Integer.MAX_VALUE);
    }

    public List<Produto> listarTodos(int page, int pageSize) throws SQLException {
        return listarPaginado(SELECT_ALL_ATIVOS_SQL, page, pageSize);
    }

    public List<Produto> listarTodosAdmin() throws SQLException {
        return listar(SELECT_ALL_ADMIN_SQL, null, 0, Integer.MAX_VALUE);
    }

    public List<Produto> listarTodosAdmin(int page, int pageSize) throws SQLException {
        return listarPaginado(SELECT_ALL_ADMIN_SQL, page, pageSize);
    }

    public int contarTodos() throws SQLException {
        return contar(COUNT_ALL_SQL);
    }

    public int contarAtivos() throws SQLException {
        return contar(COUNT_ATIVOS_SQL);
    }

    public int contarTodosAdmin() throws SQLException {
        return contar(COUNT_ALL_SQL);
    }

    public void atualizar(Produto produto) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setString(3, produto.getTipo());
            stmt.setString(4, produto.getUva());
            stmt.setString(5, produto.getPais());
            stmt.setString(6, produto.getRegiao());
            stmt.setDouble(7, produto.getPreco());
            stmt.setInt(8, produto.getEstoque());
            stmt.setString(9, produto.getImagem());
            stmt.setBoolean(10, produto.isAtivo());
            stmt.setObject(11, produto.getId());
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public void deletar(UUID id) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SOFT_DELETE_SQL)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public int darBaixaEstoque(UUID produtoId, int quantidade) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(BAIXAR_ESTOQUE_SQL)) {
            stmt.setInt(1, quantidade);
            stmt.setObject(2, produtoId);
            stmt.setInt(3, quantidade);
            return stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<Produto> buscarDestaques() throws SQLException {
        return listar(SELECT_DESTAQUES_SQL, null, 0, Integer.MAX_VALUE);
    }

    public List<Produto> buscarPorTipo(String tipo) throws SQLException {
        return listar(SELECT_BY_TIPO_SQL, tipo, 0, Integer.MAX_VALUE);
    }

    public List<Produto> buscarPorTipo(String tipo, int page, int pageSize) throws SQLException {
        return listarPaginadoComParam(SELECT_BY_TIPO_SQL, tipo, page, pageSize);
    }

    public int contarPorTipo(String tipo) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM produto WHERE ativo = true AND tipo = ?")) {
            stmt.setString(1, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    public List<Produto> buscar(String termo) throws SQLException {
        String like = "%" + termo.toLowerCase() + "%";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SEARCH_SQL)) {
            for (int i = 1; i <= 5; i++) stmt.setString(i, like);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Produto> produtos = new ArrayList<>();
                while (rs.next()) {
                    Produto p = mapearProduto(rs);
                    if (p != null) produtos.add(p);
                }
                return produtos;
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<Produto> buscarPorNome(String pattern, int page, int pageSize) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM produto WHERE ativo = true AND LOWER(nome) LIKE ? ORDER BY nome";
        return listarPaginadoComParam(sql, pattern, page, pageSize);
    }

    public int contarPorNome(String pattern) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM produto WHERE ativo = true AND LOWER(nome) LIKE ?")) {
            stmt.setString(1, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    public List<Produto> listarComFiltros(String tipo, Double precoMin, Double precoMax, String ordem, int page, int pageSize) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM produto WHERE ativo = true");
        List<Object> params = new ArrayList<>();

        if (tipo != null && !tipo.isBlank()) {
            sql.append(" AND tipo = ?");
            params.add(tipo);
        }
        if (precoMin != null) {
            sql.append(" AND preco >= ?");
            params.add(precoMin);
        }
        if (precoMax != null) {
            sql.append(" AND preco <= ?");
            params.add(precoMax);
        }

        String orderClause;
        if (ordem != null) {
            switch (ordem) {
                case "preco_asc": orderClause = "preco ASC"; break;
                case "preco_desc": orderClause = "preco DESC"; break;
                case "nome_asc": orderClause = "nome ASC"; break;
                case "recentes": orderClause = "data_criacao DESC"; break;
                default: orderClause = "nome ASC";
            }
        } else {
            orderClause = "nome ASC";
        }
        sql.append(" ORDER BY ").append(orderClause);

        int offset = (page - 1) * pageSize;
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        List<Produto> produtos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = mapearProduto(rs);
                    if (p != null) produtos.add(p);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return produtos;
    }

    public int contarComFiltros(String tipo, Double precoMin, Double precoMax) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM produto WHERE ativo = true");
        List<Object> params = new ArrayList<>();

        if (tipo != null && !tipo.isBlank()) {
            sql.append(" AND tipo = ?");
            params.add(tipo);
        }
        if (precoMin != null) {
            sql.append(" AND preco >= ?");
            params.add(precoMin);
        }
        if (precoMax != null) {
            sql.append(" AND preco <= ?");
            params.add(precoMax);
        }

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    private List<Produto> listar(String sql, String param, int offset, int limit) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (param != null) stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = mapearProduto(rs);
                    if (p != null) produtos.add(p);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return produtos;
    }

    private List<Produto> listarPaginado(String sql, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        List<Produto> produtos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String paginado = sql + " LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(paginado)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = mapearProduto(rs);
                    if (p != null) produtos.add(p);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return produtos;
    }

    private List<Produto> listarPaginadoComParam(String sql, String param, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        List<Produto> produtos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String paginado = sql + " LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(paginado)) {
            stmt.setString(1, param);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = mapearProduto(rs);
                    if (p != null) produtos.add(p);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return produtos;
    }

    private int contar(String sql) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        try {
            Produto p = new Produto();
            p.setId(rs.getObject("id", UUID.class));
            if (p.getId() == null) return null;
            p.setNome(rs.getString("nome"));
            p.setDescricao(rs.getString("descricao"));
            p.setTipo(rs.getString("tipo"));
            p.setUva(rs.getString("uva"));
            p.setPais(rs.getString("pais"));
            p.setRegiao(rs.getString("regiao"));
            p.setPreco(rs.getDouble("preco"));
            p.setEstoque(rs.getInt("estoque"));
            p.setImagem(rs.getString("imagem"));
            p.setAtivo(rs.getBoolean("ativo"));
            Timestamp dc = rs.getTimestamp("data_criacao");
            if (dc != null) p.setDataCriacao(dc.toLocalDateTime());
            return p;
        } catch (SQLException e) {
            return null;
        }
    }
}
