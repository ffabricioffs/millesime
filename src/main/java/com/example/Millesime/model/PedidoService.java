package com.example.Millesime.model;

import com.example.Millesime.exception.ResourceNotFoundException;
import com.example.Millesime.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PedidoService {

    private static final Set<String> STATUS_VALIDOS = Set.of("PENDENTE", "CONFIRMADO", "ENVIADO", "ENTREGUE", "CANCELADO");
    private static final Set<String> TRANICOES_CANCELAMENTO = Set.of("PENDENTE", "CONFIRMADO");

    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final DataSource dataSource;

    public PedidoService(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO,
                         ProdutoDAO produtoDAO, DataSource dataSource) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
        this.dataSource = dataSource;
    }

    public Pedido criarPedido(UUID clienteId, List<ItemPedido> itens) throws Exception {
        if (clienteId == null) {
            throw new ValidationException("Cliente nao informado.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new ValidationException("Carrinho vazio.");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);

        double total = 0.0;

        for (ItemPedido item : itens) {
            if (item.getProdutoId() == null) {
                throw new ValidationException("Produto nao informado no item.");
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new ValidationException("Quantidade invalida para o produto.");
            }

            Produto produto = produtoDAO.buscarPorId(item.getProdutoId());
            if (produto == null || !produto.isAtivo()) {
                throw new ResourceNotFoundException("Produto nao encontrado: " + item.getProdutoId());
            }
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new ValidationException("Estoque insuficiente para: " + produto.getNome());
            }

            item.setPrecoUnitario(produto.getPreco());
            item.setNomeProduto(produto.getNome());
            total += item.getQuantidade() * item.getPrecoUnitario();
        }

        pedido.setTotal(total);
        pedidoDAO.salvar(pedido);

        for (ItemPedido item : itens) {
            item.setPedidoId(pedido.getId());
            itemPedidoDAO.salvar(item);
        }

        darBaixaEstoque(itens);

        pedido.setItens(itens);
        return pedido;
    }

    public Pedido buscarPorId(UUID id) throws Exception {
        if (id == null) {
            throw new ValidationException("Id do pedido e obrigatorio.");
        }
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido == null) {
                throw new ResourceNotFoundException("Pedido nao encontrado.");
            }
            pedido.setItens(itemPedidoDAO.buscarPorPedidoId(id));
            return pedido;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar pedido.", e);
        }
    }

    public List<Pedido> listarPorCliente(UUID clienteId, int page, int pageSize) throws Exception {
        if (clienteId == null) {
            throw new ValidationException("Cliente nao informado.");
        }
        try {
            return pedidoDAO.buscarPorClienteId(clienteId, page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Erro ao listar pedidos do cliente.", e);
        }
    }

    public int contarPorCliente(UUID clienteId) throws Exception {
        try {
            return pedidoDAO.contarPorCliente(clienteId);
        } catch (SQLException e) {
            throw new Exception("Erro ao contar pedidos do cliente.", e);
        }
    }

    public List<Pedido> listarTodos(int page, int pageSize) throws Exception {
        try {
            return pedidoDAO.listarTodos(page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Erro ao listar pedidos.", e);
        }
    }

    public int contarTodos() throws Exception {
        try {
            return pedidoDAO.contarTodos();
        } catch (SQLException e) {
            throw new Exception("Erro ao contar pedidos.", e);
        }
    }

    public void atualizarStatus(UUID id, String novoStatus) throws Exception {
        if (id == null || novoStatus == null || novoStatus.isBlank()) {
            throw new ValidationException("Id e status sao obrigatorios.");
        }
        if (!STATUS_VALIDOS.contains(novoStatus.toUpperCase())) {
            throw new ValidationException("Status invalido: " + novoStatus);
        }

        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido == null) {
                throw new ResourceNotFoundException("Pedido nao encontrado.");
            }

            String statusAtual = pedido.getStatus();
            String statusDestino = novoStatus.toUpperCase();

            if ("CANCELADO".equals(statusDestino)) {
                if (!TRANICOES_CANCELAMENTO.contains(statusAtual)) {
                    throw new ValidationException(
                        "Nao e possivel cancelar um pedido " + statusAtual + ".");
                }
                reporEstoque(id);
            }

            pedidoDAO.atualizarStatus(id, statusDestino);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar status do pedido.", e);
        }
    }

    public void cancelarPedido(UUID id) throws Exception {
        atualizarStatus(id, "CANCELADO");
    }

    private void darBaixaEstoque(List<ItemPedido> itens) throws Exception {
        String sql = "UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ItemPedido item : itens) {
                statement.setInt(1, item.getQuantidade());
                statement.setObject(2, item.getProdutoId());
                statement.setInt(3, item.getQuantidade());
                int rows = statement.executeUpdate();
                if (rows == 0) {
                    throw new ValidationException(
                        "Estoque insuficiente para o produto: " + item.getNomeProduto());
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao dar baixa no estoque.", e);
        }
    }

    private void reporEstoque(UUID pedidoId) throws Exception {
        List<ItemPedido> itens = itemPedidoDAO.buscarPorPedidoId(pedidoId);
        String sql = "UPDATE produto SET estoque = estoque + ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ItemPedido item : itens) {
                statement.setInt(1, item.getQuantidade());
                statement.setObject(2, item.getProdutoId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao repor estoque.", e);
        }
    }
}
