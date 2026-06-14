package com.example.Millesime.config;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class AdminBootstrap implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public AdminBootstrap(DataSource dataSource, PasswordEncoder passwordEncoder, Environment env) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@millesime.com.br";
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM cliente WHERE email = ?",
            Integer.class, adminEmail
        );
        if (count != null && count > 0) {
            return;
        }

        String rawPassword = env.getProperty("ADMIN_PASSWORD", "admin123");
        UUID adminId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO cliente (id, nome_completo, email, senha, cpf, ativo) VALUES (?, ?, ?, ?, ?, ?)",
            adminId, "Administrador Millésime", adminEmail,
            passwordEncoder.encode(rawPassword), "00000000000", true
        );
        jdbcTemplate.update(
            "INSERT INTO cliente_role (id, cliente_id, role) VALUES (?, ?, ?)",
            UUID.randomUUID(), adminId, "ROLE_ADMIN"
        );
    }
}
