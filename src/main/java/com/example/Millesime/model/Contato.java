package com.example.Millesime.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Contato {

    private UUID id;
    private String nome;
    private String email;
    private String assunto;
    private String mensagem;
    private LocalDateTime dataContato;

    public Contato() {
        this.id = UUID.randomUUID();
        this.dataContato = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getDataContato() { return dataContato; }
    public void setDataContato(LocalDateTime dataContato) { this.dataContato = dataContato; }
}
