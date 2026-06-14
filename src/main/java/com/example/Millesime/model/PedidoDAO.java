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

@Repository
public class PedidoDAO {

    private static final String INSERT_SQL = "INSERT INTO pedido (id, cliente_id, data_pedido, total, status) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM pedido WHERE id = ?";
    private static final String SELECT_BY_CLIENTE_SQL = "SELECT * FROM pedido WHERE cliente_id = ? ORDER BY data_pedido DESC LIMIT ? OFFSET ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM pedido ORDER BY data_pedido DESC LIMIT ? OFFSET ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE pedido SET status = ? WHERE id = ?";
    private static final String COUNT_BY_CLIENTE_SQL = "SELECT COUNT(*) FROM pedido WHERE cliente_id = ?";
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM pedido";

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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, pedido.getId());
            statement.setObject(2, pedido.getClienteId());
            statement.setTimestamp(3, Timestamp.valueOf(pedido.getDataPedido()));
            statement.setDouble(4, pedido.getTotal());
            statement.setString(5, pedido.getStatus());
            statement.executeUpdate();
        }
    }

    public Pedido buscarPorId(UUID id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearPedido(resultSet);
                }
            }
        }
        return null;
    }

    public List<Pedido> buscarPorClienteId(UUID clienteId, int page, int pageSize) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        int offset = Math.max(0, page - 1) * pageSize;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_CLIENTE_SQL)) {
            statement.setObject(1, clienteId);
            statement.setInt(2, pageSize);
            statement.setInt(3, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pedidos.add(mapearPedido(resultSet));
                }
            }
        }
        return pedidos;
    }

    public List<Pedido> listarTodos(int page, int pageSize) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        int offset = Math.max(0, page - 1) * pageSize;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL)) {
            statement.setInt(1, pageSize);
            statement.setInt(2, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pedidos.add(mapearPedido(resultSet));
                }
            }
        }
        return pedidos;
    }

    public void atualizarStatus(UUID id, String status) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS_SQL)) {
            statement.setString(1, status);
            statement.setObject(2, id);
            statement.executeUpdate();
        }
    }

    public int contarPorCliente(UUID clienteId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BY_CLIENTE_SQL)) {
            statement.setObject(1, clienteId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    public int contarTodos() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    private Pedido mapearPedido(ResultSet resultSet) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(resultSet.getObject("id", UUID.class));
        pedido.setClienteId(resultSet.getObject("cliente_id", UUID.class));
        Timestamp dataPedido = resultSet.getTimestamp("data_pedido");
        if (dataPedido != null) {
            pedido.setDataPedido(dataPedido.toLocalDateTime());
        }
        pedido.setTotal(resultSet.getDouble("total"));
        pedido.setStatus(resultSet.getString("status"));
        return pedido;
    }
}
