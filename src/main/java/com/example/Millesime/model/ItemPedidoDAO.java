package com.example.Millesime.model;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ItemPedidoDAO {

    private static final String INSERT_SQL = "INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_PEDIDO_SQL = "SELECT ip.*, p.nome AS produto_nome FROM item_pedido ip JOIN produto p ON p.id = ip.produto_id WHERE ip.pedido_id = ?";

    private final DataSource dataSource;

    public ItemPedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(ItemPedido item) throws SQLException {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, item.getId());
            statement.setObject(2, item.getPedidoId());
            statement.setObject(3, item.getProdutoId());
            statement.setInt(4, item.getQuantidade());
            statement.setDouble(5, item.getPrecoUnitario());
            statement.executeUpdate();
        }
    }

    public List<ItemPedido> buscarPorPedidoId(UUID pedidoId) throws SQLException {
        List<ItemPedido> itens = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_PEDIDO_SQL)) {
            statement.setObject(1, pedidoId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    itens.add(mapearItem(resultSet));
                }
            }
        }
        return itens;
    }

    private ItemPedido mapearItem(ResultSet resultSet) throws SQLException {
        ItemPedido item = new ItemPedido();
        item.setId(resultSet.getObject("id", UUID.class));
        item.setPedidoId(resultSet.getObject("pedido_id", UUID.class));
        item.setProdutoId(resultSet.getObject("produto_id", UUID.class));
        item.setQuantidade(resultSet.getInt("quantidade"));
        item.setPrecoUnitario(resultSet.getDouble("preco_unitario"));
        try {
            item.setNomeProduto(resultSet.getString("produto_nome"));
        } catch (SQLException e) {
            // campo opcional do JOIN
        }
        return item;
    }
}
