# Script para configurar apenas as variáveis de ambiente
# Use este script quando quiser rodar comandos Maven manualmente

# Define as variáveis de ambiente para desenvolvimento
$env:DB_URL = "jdbc:postgresql://dpg-d7jph7vavr4c73chk1e0-a.ohio-postgres.render.com/millesime"
$env:DB_USERNAME = "admin"
$env:DB_PASSWORD = "MyylWWoU2uiPAqmcCbIa3l0PF9CqNuMP"
$env:SERVER_PORT = "8081"
$env:HIBERNATE_DDL_AUTO = "create-drop"
$env:SQL_INIT_MODE = "always"

Write-Host "✓ Variáveis de ambiente configuradas com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Agora você pode executar:" -ForegroundColor Cyan
Write-Host "  .\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=`"--spring.profiles.active=dev`""
Write-Host "  .\mvnw.cmd clean compile"
Write-Host "  .\mvnw.cmd test"
Write-Host ""
