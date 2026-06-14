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
public class PedidoDAO {

    private static final String COLUNAS = "id, cliente_id, data_pedido, status, total";
    private static final String INSERT_SQL = "INSERT INTO pedido (id, cliente_id, data_pedido, status, total) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT " + COLUNAS + " FROM pedido WHERE id = ?";
    private static final String SELECT_BY_CLIENTE_SQL = "SELECT " + COLUNAS + " FROM pedido WHERE cliente_id = ? ORDER BY data_pedido DESC";
    private static final String SELECT_ALL_SQL = "SELECT " + COLUNAS + " FROM pedido ORDER BY data_pedido DESC";
    private static final String SELECT_RECENT_SQL = "SELECT " + COLUNAS + " FROM pedido ORDER BY data_pedido DESC LIMIT 5";
    private static final String UPDATE_STATUS_SQL = "UPDATE pedido SET status = ? WHERE id = ?";
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM pedido";
    private static final String COUNT_BY_CLIENTE_SQL = "SELECT COUNT(*) FROM pedido WHERE cliente_id = ?";
    private static final String SUM_TOTAL_SQL = "SELECT COALESCE(SUM(total), 0) FROM pedido WHERE status != 'CANCELADO'";

    private final DataSource dataSource;

    public PedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(Pedido pedido) throws SQLException {
        if (pedido.getId() == null) {
            pedido.setId(UUID.randomUUID());
        }
        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setObject(1, pedido.getId());
            stmt.setObject(2, pedido.getClienteId());
            stmt.setTimestamp(3, Timestamp.valueOf(pedido.getDataPedido()));
            stmt.setString(4, pedido.getStatus());
            stmt.setDouble(5, pedido.getTotal());
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Pedido buscarPorId(UUID id) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearPedido(rs);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return null;
    }

    public List<Pedido> listarPorCliente(UUID clienteId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_CLIENTE_SQL)) {
            stmt.setObject(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return pedidos;
    }

    public List<Pedido> listarTodos() throws SQLException {
        return listar(SELECT_ALL_SQL, null, 0, Integer.MAX_VALUE);
    }

    public List<Pedido> listarTodos(int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        List<Pedido> pedidos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = SELECT_ALL_SQL + " LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return pedidos;
    }

    public List<Pedido> buscarPorClienteId(UUID clienteId, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        List<Pedido> pedidos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT " + COLUNAS + " FROM pedido WHERE cliente_id = ? ORDER BY data_pedido DESC LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, clienteId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return pedidos;
    }

    public List<Pedido> listarRecentes() throws SQLException {
        return listar(SELECT_RECENT_SQL, null, 0, Integer.MAX_VALUE);
    }

    public int contarTodos() throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    public int contarPorCliente(UUID clienteId) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_BY_CLIENTE_SQL)) {
            stmt.setObject(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0;
    }

    public double somarTotal() throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SUM_TOTAL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return 0.0;
    }

    public void atualizarStatus(UUID id, String status) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status);
            stmt.setObject(2, id);
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private List<Pedido> listar(String sql, Object param, int limit, int offset) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (param != null) stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return pedidos;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getObject("id", UUID.class));
        p.setClienteId(rs.getObject("cliente_id", UUID.class));
        Timestamp dp = rs.getTimestamp("data_pedido");
        if (dp != null) p.setDataPedido(dp.toLocalDateTime());
        p.setStatus(rs.getString("status"));
        p.setTotal(rs.getDouble("total"));
        return p;
    }
}
