# Script para configurar variáveis de ambiente e executar a aplicação Millesime

# Define as variáveis de ambiente
$env:DB_URL = "jdbc:postgresql://dpg-d7jph7vavr4c73chk1e0-a.ohio-postgres.render.com/millesime"
$env:DB_USERNAME = "admin"
$env:DB_PASSWORD = "MyylWWoU2uiPAqmcCbIa3l0PF9CqNuMP"
$env:SERVER_PORT = "8081"
$env:HIBERNATE_DDL_AUTO = "create-drop"
$env:SQL_INIT_MODE = "always"

# Exibe as variáveis configuradas
Write-Host "[OK] Variáveis de ambiente configuradas:" -ForegroundColor Green
Write-Host "  DB_URL: $env:DB_URL"
Write-Host "  DB_USERNAME: $env:DB_USERNAME"
Write-Host "  SERVER_PORT: $env:SERVER_PORT"
Write-Host ""

# Executa a aplicação com perfil de desenvolvimento
Write-Host "Iniciando aplicação Spring Boot..." -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
