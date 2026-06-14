package com.example.Millesime.model;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class ContatoDAO {

    private static final String INSERT_SQL = "INSERT INTO contato (id, nome, email, assunto, mensagem, data_contato) VALUES (?, ?, ?, ?, ?, ?)";

    private final DataSource dataSource;

    public ContatoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void salvar(Contato contato) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setObject(1, contato.getId());
            stmt.setString(2, truncate(contato.getNome(), 100));
            stmt.setString(3, truncate(contato.getEmail(), 100));
            stmt.setString(4, truncate(contato.getAssunto(), 200));
            stmt.setString(5, truncate(contato.getMensagem(), 2000));
            stmt.setTimestamp(6, Timestamp.valueOf(contato.getDataContato()));
            stmt.executeUpdate();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}
