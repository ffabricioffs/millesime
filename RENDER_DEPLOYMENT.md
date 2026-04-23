# Deployment no Render - Guia de Configuração

## Variáveis de Ambiente Obrigatórias

Ao fazer deploy no Render, configure as seguintes variáveis de ambiente no painel do Render:

### Database Configuration
- **DATABASE_URL**: URL completa da conexão PostgreSQL
  - Formato: `jdbc:postgresql://host:port/database`
  - Exemplo: `jdbc:postgresql://dpg-xxxxx.ohio-postgres.render.com/millesime`

- **DB_USERNAME**: Usuário do PostgreSQL
  - Exemplo: `admin`

- **DB_PASSWORD**: Senha do PostgreSQL
  - Exemplo: (copie do Render PostgreSQL dashboard)

### Application Configuration
- **SPRING_PROFILES_ACTIVE**: `prod` (usa application-prod.yaml)

- **PORT**: (opcional) Porta de execução, padrão é 8081
  - Render pode automaticamente atribuir via variável `$PORT`

## Como fazer deploy

### Via GitHub (Recomendado)
1. Push das mudanças para o repositório
2. Conecte o repositório ao Render
3. Configure as variáveis de ambiente no Render dashboard
4. Render detectará automaticamente o `Dockerfile`

### Via Docker CLI
```bash
docker build -t millesime .
docker run -e DATABASE_URL=jdbc:postgresql://... \
           -e DB_USERNAME=admin \
           -e DB_PASSWORD=xxxxx \
           -e SPRING_PROFILES_ACTIVE=prod \
           -p 8081:8081 \
           millesime
```

## Troubleshooting

### Aplicação desliga logo após iniciar
- Verifique se as variáveis de ambiente `DATABASE_URL`, `DB_USERNAME` e `DB_PASSWORD` estão configuradas
- Verifique a conectividade com o banco de dados PostgreSQL

### Porta não está aberta
- Certifique-se de que `SPRING_PROFILES_ACTIVE=prod` está configurado
- Ou manualmente passe `-Dserver.port=${PORT:8081}` como propriedade Java

### Erros de conexão com banco
- Teste a URL de conexão localmente
- Verifique se o firewall permite conexão
- Confirme username e password
