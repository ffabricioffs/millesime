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
        config.addDataSourceProperty("sslmode", env.getProperty("DB_SSL_MODE", "require"));
        config.addDataSourceProperty("ssl", env.getProperty("DB_SSL", "true"));

        return new HikariDataSource(config);
    }

    private PostgresUrlInfo resolvePostgresUrl(Environment env) {
        String url = env.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(url)) {
            url = env.getProperty("DB_URL");
        }

        if (!StringUtils.hasText(url)) {
            url = env.getProperty("SPRING_DATASOURCE_URL");
        }

        if (!StringUtils.hasText(url)) {
            throw new IllegalStateException(
                    "Database URL is not configured. Set DATABASE_URL, DB_URL, or SPRING_DATASOURCE_URL.");
        }

        return normalizePostgresUrl(url);
    }

    private PostgresUrlInfo normalizePostgresUrl(String url) {
        if (url.startsWith("postgres://")) {
            try {
                URI uri = new URI(url);
                String username = null;
                String password = null;
                if (uri.getUserInfo() != null) {
                    String[] parts = uri.getUserInfo().split(":", 2);
                    username = parts[0];
                    if (parts.length > 1) {
                        password = parts[1];
                    }
                }

                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");
                jdbcUrl.append(uri.getHost());
                if (uri.getPort() != -1) {
                    jdbcUrl.append(':').append(uri.getPort());
                }
                if (uri.getPath() != null) {
                    jdbcUrl.append(uri.getPath());
                }
                if (uri.getQuery() != null) {
                    jdbcUrl.append('?').append(uri.getQuery());
                }

                return new PostgresUrlInfo(jdbcUrl.toString(), username, password);
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Invalid DATABASE_URL format: " + url, e);
            }
        }
        return new PostgresUrlInfo(url, null, null);
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
