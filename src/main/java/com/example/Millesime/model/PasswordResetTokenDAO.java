package com.example.Millesime.model;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class PasswordResetTokenDAO {

    private static final String INSERT_SQL = """
            INSERT INTO password_reset_token (id, cliente_id, token, expiration, used)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_TOKEN_SQL = "SELECT * FROM password_reset_token WHERE token = ?";
    private static final String INVALIDATE_BY_CLIENTE_SQL = "UPDATE password_reset_token SET used = true WHERE cliente_id = ? AND used = false";
    private static final String MARK_AS_USED_SQL = "UPDATE password_reset_token SET used = true WHERE id = ?";

    private static final Logger log = LoggerFactory.getLogger(PasswordResetTokenDAO.class);

    private final DataSource dataSource;

    public PasswordResetTokenDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(PasswordResetToken token) throws SQLException {
        if (token.getId() == null) {
            token.setId(UUID.randomUUID());
        }
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, token.getId());
            statement.setObject(2, token.getClienteId());
            statement.setString(3, token.getToken());
            statement.setTimestamp(4, Timestamp.valueOf(token.getExpiration()));
            statement.setBoolean(5, token.isUsed());
            statement.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public PasswordResetToken buscarPorToken(String token) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_TOKEN_SQL)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearToken(resultSet);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return null;
    }

    public void marcarComoUsado(UUID id) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(MARK_AS_USED_SQL)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public void invalidarTokens(UUID clienteId) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement statement = connection.prepareStatement(INVALIDATE_BY_CLIENTE_SQL)) {
            statement.setObject(1, clienteId);
            statement.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private PasswordResetToken mapearToken(ResultSet resultSet) {
        try {
            PasswordResetToken token = new PasswordResetToken();
            token.setId(resultSet.getObject("id", UUID.class));
            if (token.getId() == null) return null;
            token.setClienteId(resultSet.getObject("cliente_id", UUID.class));
            token.setToken(resultSet.getString("token"));
            Timestamp expiration = resultSet.getTimestamp("expiration");
            if (expiration != null) {
                token.setExpiration(expiration.toLocalDateTime());
            }
            token.setUsed(resultSet.getBoolean("used"));
            return token;
        } catch (SQLException e) {
            log.error("Erro ao mapear token de redefinição", e);
            return null;
        }
    }
}
