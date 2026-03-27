package com.example.Millesime.model;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ClienteDAO {

    private static final String INSERT_SQL = """
            INSERT INTO cliente (
                id, nome_completo, email, senha, cpf, data_nascimento,
                telefone, newsletter, data_cadastro, ativo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM cliente WHERE id = ?";
    private static final String SELECT_BY_EMAIL_SQL = "SELECT * FROM cliente WHERE email = ?";
    private static final String SELECT_BY_CPF_SQL = "SELECT * FROM cliente WHERE cpf = ?";
    private static final String SELECT_ALL_ACTIVE_SQL = "SELECT * FROM cliente WHERE ativo = true ORDER BY nome_completo";
    private static final String UPDATE_SQL = """
            UPDATE cliente
            SET nome_completo = ?, email = ?, senha = ?, cpf = ?, data_nascimento = ?,
                telefone = ?, newsletter = ?, ativo = ?
            WHERE id = ?
            """;
    private static final String SOFT_DELETE_SQL = "UPDATE cliente SET ativo = false WHERE id = ?";

    private final DataSource dataSource;

    public ClienteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(Cliente cliente) throws SQLException {
        if (cliente.getId() == null) {
            cliente.setId(UUID.randomUUID());
        }
        if (cliente.getDataCadastro() == null) {
            cliente.setDataCadastro(LocalDateTime.now());
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            preencherStatementParaSalvar(statement, cliente);
            statement.executeUpdate();
        }
    }

    public Cliente buscarPorId(UUID id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setObject(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearCliente(resultSet);
                }
            }
        }

        return null;
    }

    public Cliente buscarPorEmail(String email) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearCliente(resultSet);
                }
            }
        }

        return null;
    }

    public Cliente buscarPorCpf(String cpf) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_CPF_SQL)) {
            statement.setString(1, cpf);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearCliente(resultSet);
                }
            }
        }

        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ACTIVE_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clientes.add(mapearCliente(resultSet));
            }
        }

        return clientes;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, cliente.getNomeCompleto());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getSenha());
            statement.setString(4, cliente.getCpf());

            if (cliente.getDataNascimento() != null) {
                statement.setDate(5, Date.valueOf(cliente.getDataNascimento()));
            } else {
                statement.setNull(5, java.sql.Types.DATE);
            }

            statement.setString(6, cliente.getTelefone());
            statement.setBoolean(7, cliente.isNewsletter());
            statement.setBoolean(8, cliente.isAtivo());
            statement.setObject(9, cliente.getId());
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

    public Cliente autenticar(String email, String senha) throws SQLException {
        Cliente cliente = buscarPorEmail(email);
        if (cliente != null && cliente.isAtivo() && cliente.getSenha().equals(senha)) {
            return cliente;
        }
        return null;
    }

    private void preencherStatementParaSalvar(PreparedStatement statement, Cliente cliente) throws SQLException {
        statement.setObject(1, cliente.getId());
        statement.setString(2, cliente.getNomeCompleto());
        statement.setString(3, cliente.getEmail());
        statement.setString(4, cliente.getSenha());
        statement.setString(5, cliente.getCpf());

        if (cliente.getDataNascimento() != null) {
            statement.setDate(6, Date.valueOf(cliente.getDataNascimento()));
        } else {
            statement.setNull(6, java.sql.Types.DATE);
        }

        statement.setString(7, cliente.getTelefone());
        statement.setBoolean(8, cliente.isNewsletter());
        statement.setTimestamp(9, Timestamp.valueOf(cliente.getDataCadastro()));
        statement.setBoolean(10, cliente.isAtivo());
    }

    private Cliente mapearCliente(ResultSet resultSet) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(resultSet.getObject("id", UUID.class));
        cliente.setNomeCompleto(resultSet.getString("nome_completo"));
        cliente.setEmail(resultSet.getString("email"));
        cliente.setSenha(resultSet.getString("senha"));
        cliente.setCpf(resultSet.getString("cpf"));

        Date dataNascimento = resultSet.getDate("data_nascimento");
        if (dataNascimento != null) {
            cliente.setDataNascimento(dataNascimento.toLocalDate());
        }

        cliente.setTelefone(resultSet.getString("telefone"));
        cliente.setNewsletter(resultSet.getBoolean("newsletter"));

        Timestamp dataCadastro = resultSet.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            cliente.setDataCadastro(dataCadastro.toLocalDateTime());
        }

        cliente.setAtivo(resultSet.getBoolean("ativo"));
        return cliente;
    }
}