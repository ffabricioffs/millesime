package com.example.Millesime.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetToken {

    private UUID id;
    private UUID clienteId;
    private String token;
    private LocalDateTime expiration;
    private boolean used;

    public PasswordResetToken() {
        this.id = UUID.randomUUID();
        this.used = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}