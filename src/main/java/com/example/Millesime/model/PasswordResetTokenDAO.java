package com.example.Millesime.model;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Repository
public class PasswordResetTokenDAO {

    private static final String INSERT_SQL = "INSERT INTO password_reset_token (id, cliente_id, token, expiration, used) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_TOKEN_SQL = "SELECT * FROM password_reset_token WHERE token = ?";
    private static final String UPDATE_USED_SQL = "UPDATE password_reset_token SET used = ? WHERE id = ?";

    private final DataSource dataSource;

    public PasswordResetTokenDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(PasswordResetToken token) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, token.getId());
            statement.setObject(2, token.getClienteId());
            statement.setString(3, token.getToken());
            statement.setTimestamp(4, Timestamp.valueOf(token.getExpiration()));
            statement.setBoolean(5, token.isUsed());
            statement.executeUpdate();
        }
    }

    public PasswordResetToken buscarPorToken(String tokenString) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_TOKEN_SQL)) {
            statement.setString(1, tokenString);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PasswordResetToken token = new PasswordResetToken();
                    token.setId(resultSet.getObject("id", UUID.class));
                    token.setClienteId(resultSet.getObject("cliente_id", UUID.class));
                    token.setToken(resultSet.getString("token"));
                    token.setExpiration(resultSet.getTimestamp("expiration").toLocalDateTime());
                    token.setUsed(resultSet.getBoolean("used"));
                    return token;
                }
            }
        }
        return null;
    }

    public void marcarComoUsado(UUID id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USED_SQL)) {
            statement.setBoolean(1, true);
            statement.setObject(2, id);
            statement.executeUpdate();
        }
    }
}
