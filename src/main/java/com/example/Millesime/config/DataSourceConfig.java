package com.example.Millesime.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        PostgresUrlInfo urlInfo = resolvePostgresUrl(env);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(urlInfo.jdbcUrl);
        config.setDriverClassName(env.getProperty("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "org.postgresql.Driver"));

        String username = env.getProperty("DB_USERNAME");
        if (!StringUtils.hasText(username)) {
            username = urlInfo.username;
        }
        if (StringUtils.hasText(username)) {
            config.setUsername(username);
        }

        String password = env.getProperty("DB_PASSWORD");
        if (!StringUtils.hasText(password)) {
            password = urlInfo.password;
        }
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }

        config.setMaximumPoolSize(Integer.parseInt(env.getProperty("DB_MAX_POOL_SIZE", "10")));
        config.setMinimumIdle(Integer.parseInt(env.getProperty("DB_MIN_IDLE", "2")));
        config.addDataSourceProperty("sslmode", env.getProperty("DB_SSL_MODE", "prefer"));

        return new HikariDataSource(config);
    }

    private PostgresUrlInfo resolvePostgresUrl(Environment env) {
        // Verifica DATABASE_URL (formato Render: postgres://...)
        String url = env.getProperty("DATABASE_URL");
        
        // Se não encontrou DATABASE_URL, tenta DB_URL
        if (!StringUtils.hasText(url)) {
            url = env.getProperty("DB_URL");
        }

        // Se não encontrou DB_URL, tenta SPRING_DATASOURCE_URL
        if (!StringUtils.hasText(url)) {
            url = env.getProperty("SPRING_DATASOURCE_URL");
        }

        if (!StringUtils.hasText(url)) {
            // Tenta montar a URL a partir das variáveis separadas do Render
            String host = env.getProperty("PGHOST");
            String port = env.getProperty("PGPORT", "5432");
            String database = env.getProperty("PGDATABASE");
            
            if (StringUtils.hasText(host) && StringUtils.hasText(database)) {
                String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
                return new PostgresUrlInfo(
                    jdbcUrl,
                    env.getProperty("PGUSER"),
                    env.getProperty("PGPASSWORD")
                );
            }
            
            throw new IllegalStateException(
                    "Database URL is not configured. Set DATABASE_URL, DB_URL, SPRING_DATASOURCE_URL, or PGHOST/PGDATABASE variables.");
        }

        // Se encontrou DATABASE_URL no formato postgres://, converte para JDBC
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            return convertToJdbcUrl(url);
        }
        
        // Se já está no formato JDBC, retorna diretamente
        return new PostgresUrlInfo(url, null, null);
    }

    private PostgresUrlInfo convertToJdbcUrl(String url) {
        try {
            URI uri = new URI(url);
            String username = null;
            String password = null;
            
            // Extrai credenciais da URL
            if (uri.getUserInfo() != null) {
                String[] parts = uri.getUserInfo().split(":", 2);
                username = parts[0];
                if (parts.length > 1) {
                    password = parts[1];
                }
            }

            // Monta a URL JDBC SEM credenciais
            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");
            jdbcUrl.append(uri.getHost());
            
            if (uri.getPort() != -1) {
                jdbcUrl.append(':').append(uri.getPort());
            }
            
            if (uri.getPath() != null && !uri.getPath().isEmpty()) {
                // Remove a barra inicial se necessário
                String path = uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();
                jdbcUrl.append('/').append(path);
            }
            
            // Adiciona parâmetros de query (como sslmode)
            if (uri.getQuery() != null) {
                jdbcUrl.append('?').append(uri.getQuery());
            } else {
                // Garante SSL por padrão
                jdbcUrl.append("?sslmode=require");
            }

            return new PostgresUrlInfo(jdbcUrl.toString(), username, password);
            
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid DATABASE_URL format: " + url, e);
        }
    }

    private static class PostgresUrlInfo {
        final String jdbcUrl;
        final String username;
        final String password;

        PostgresUrlInfo(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }
    }
}