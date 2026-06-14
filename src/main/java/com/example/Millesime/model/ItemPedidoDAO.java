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

import org.springframework.jdbc.datasource.DataSourceUtils;

@Repository
public class ItemPedidoDAO {

    private static final String INSERT_SQL = "INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_PEDIDO_SQL = """
            SELECT ip.*, p.nome as nome_produto
            FROM item_pedido ip
            JOIN produto p ON p.id = ip.produto_id
            WHERE ip.pedido_id = ?
            """;

    private final DataSource dataSource;

    public ItemPedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(ItemPedido item) throws SQLException {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setObject(1, item.getId());
            stmt.setObject(2, item.getPedidoId());
            stmt.setObject(3, item.getProdutoId());
            stmt.setInt(4, item.getQuantidade());
            stmt.setDouble(5, item.getPrecoUnitario());
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<ItemPedido> buscarPorPedidoId(UUID pedidoId) throws SQLException {
        List<ItemPedido> itens = new ArrayList<>();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_PEDIDO_SQL)) {
            stmt.setObject(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearItemPedido(rs));
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return itens;
    }

    private ItemPedido mapearItemPedido(ResultSet rs) throws SQLException {
        ItemPedido item = new ItemPedido();
        item.setId(rs.getObject("id", UUID.class));
        item.setPedidoId(rs.getObject("pedido_id", UUID.class));
        item.setProdutoId(rs.getObject("produto_id", UUID.class));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitario(rs.getDouble("preco_unitario"));
        item.setNomeProduto(rs.getString("nome_produto"));
        return item;
    }
}
