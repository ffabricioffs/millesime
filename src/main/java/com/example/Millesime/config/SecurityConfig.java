package com.example.Millesime.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
            "SELECT email, senha, ativo FROM cliente WHERE LOWER(email) = LOWER(?)"
        );
        manager.setAuthoritiesByUsernameQuery(
            "SELECT c.email, cr.role FROM cliente c " +
            "JOIN cliente_role cr ON c.id = cr.cliente_id " +
            "WHERE LOWER(c.email) = LOWER(?)"
        );
        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/register-success",
                    "/reset-password/**", "/catalogo", "/produto/**",
                    "/busca", "/sobre", "/contato", "/enviar-contato", "/politica-privacidade",
                    "/newsletter", "/carrinho", "/carrinho/adicionar", "/carrinho/remover",
                    "/css/**", "/js/**", "/error/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/clientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("ADMIN")
                .requestMatchers("/api/pedidos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/produtos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; img-src 'self' https://i.ibb.co https://d2xsxph8kpxj0f.cloudfront.net; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; script-src 'self'; frame-src https://www.openstreetmap.org")
                )
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .build();
    }
}
