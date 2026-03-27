# Guia de Integração - Millésime Spring Boot

## 📋 Visão Geral

Este guia descreve como integrar os templates HTML/Thymeleaf ao projeto Spring Boot Millésime e colocá-lo em produção.

## 🚀 Início Rápido

### 1. Estrutura do Projeto

O projeto está organizado da seguinte forma:

```
Millesime_Final/
├── src/
│   ├── main/
│   │   ├── java/com/example/Millesime/
│   │   │   ├── MillesimeApplication.java      # Classe principal
│   │   │   └── controller/
│   │   │       └── HomeController.java        # Controlador de rotas
│   │   └── resources/
│   │       ├── templates/                     # Templates Thymeleaf
│   │       │   ├── index.html                 # Página inicial
│   │       │   ├── catalog.html               # Catálogo
│   │       │   ├── product.html               # Detalhes do produto
│   │       │   ├── layout.html                # Template base
│   │       │   ├── cart.html                  # Carrinho
│   │       │   └── checkout.html              # Checkout
│   │       ├── static/                        # Recursos estáticos
│   │       │   ├── css/
│   │       │   │   └── style.css              # Estilos principais
│   │       │   └── js/
│   │       │       └── main.js                # JavaScript
│   │       └── application.properties         # Configurações
│   └── test/
├── pom.xml                                    # Dependências Maven
├── mvnw                                       # Maven Wrapper
└── README.md
```

### 2. Dependências Maven

O `pom.xml` já contém as dependências necessárias:

```xml
<!-- Thymeleaf Template Engine -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Spring Web MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

## 🔧 Configuração

### 1. Application Properties

O arquivo `application.properties` está pré-configurado com:

- **Thymeleaf**: Modo HTML, cache habilitado
- **Servidor**: Porta 8080
- **Locale**: Português Brasileiro
- **Logging**: Nível INFO

Para customizar, edite `/src/main/resources/application.properties`

### 2. Controladores

O `HomeController.java` fornece rotas básicas:

```java
GET  /              → index.html
GET  /catalogo      → catalog.html
GET  /produto/{id}  → product.html
GET  /carrinho      → cart.html
GET  /checkout      → checkout.html
GET  /sobre         → about.html
GET  /contato       → contact.html
```

## 🛠️ Desenvolvimento

### Compilar o Projeto

```bash
cd Millesime_Final
mvn clean package
```

### Executar Localmente

```bash
# Opção 1: Maven
mvn spring-boot:run

# Opção 2: JAR
java -jar target/Millesime-0.0.1-SNAPSHOT.jar
```

### Acessar a Aplicação

Abra o navegador em: `http://localhost:8080`

## 📝 Adicionar Novos Templates

### 1. Criar Template

Crie um novo arquivo em `src/main/resources/templates/`:

```html
<!-- src/main/resources/templates/about.html -->
<!DOCTYPE html>
<html lang="pt-BR" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title th:text="${pageTitle}">Sobre - Millésime</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <!-- Conteúdo -->
</body>
</html>
```

### 2. Adicionar Rota no Controlador

```java
@GetMapping("/about")
public String about(Model model) {
    model.addAttribute("pageTitle", "Sobre Nós - Millésime");
    return "about";  // Retorna about.html
}
```

## 🎨 Customizar Estilos

### Editar CSS

Modifique `/src/main/resources/static/css/style.css`:

```css
/* Alterar cores primárias */
:root {
    --primary: #5C2E3A;      /* Borgonha */
    --accent: #D4AF37;       /* Ouro */
    --secondary: #F5E6D3;    /* Bege */
}
```

### Adicionar Fontes

As fontes Google já estão importadas no template:

```html
<link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&family=Playfair+Display:wght@700;900&display=swap" rel="stylesheet">
```

## 🔗 Integração com Banco de Dados

### 1. Adicionar Dependência JPA

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 2. Configurar Banco de Dados

Em `application.properties`:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/millesime
spring.datasource.username=root
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 3. Criar Entidade

```java
package com.example.Millesime.model;

import javax.persistence.*;

@Entity
@Table(name = "wines")
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    private String region;
    private String country;
    private String type;
    private String grape;
    private Integer harvest;
    private Integer stock;
    
    // Getters e Setters
}
```

### 4. Criar Repository

```java
package com.example.Millesime.repository;

import com.example.Millesime.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {
    // Métodos customizados
}
```

### 5. Usar no Controlador

```java
@Controller
public class HomeController {
    
    @Autowired
    private WineRepository wineRepository;
    
    @GetMapping("/catalogo")
    public String catalog(Model model) {
        model.addAttribute("wines", wineRepository.findAll());
        return "catalog";
    }
}
```

## 🔐 Segurança

### 1. Adicionar Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. Configurar Segurança

```java
package com.example.Millesime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/catalogo", "/produto/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll();
        
        return http.build();
    }
}
```

## 📦 Build e Deploy

### 1. Build para Produção

```bash
mvn clean package -DskipTests
```

### 2. Criar Arquivo JAR

```bash
java -jar target/Millesime-0.0.1-SNAPSHOT.jar
```

### 3. Deploy em Servidor Linux

```bash
# Copiar JAR para servidor
scp target/Millesime-0.0.1-SNAPSHOT.jar user@server:/opt/millesime/

# Conectar ao servidor
ssh user@server

# Executar aplicação
cd /opt/millesime
java -jar Millesime-0.0.1-SNAPSHOT.jar
```

### 4. Usar Systemd (Linux)

Criar arquivo `/etc/systemd/system/millesime.service`:

```ini
[Unit]
Description=Millésime Wine Store
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/millesime
ExecStart=/usr/bin/java -jar Millesime-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Ativar serviço:

```bash
sudo systemctl enable millesime
sudo systemctl start millesime
sudo systemctl status millesime
```

## 🌐 Deploy em Docker

### 1. Criar Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Millesime-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Criar Docker Compose

```yaml
version: '3.8'

services:
  millesime:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/millesime
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=password
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=millesime
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

### 3. Build e Run

```bash
docker-compose up --build
```

## 📊 Monitoramento

### Adicionar Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Endpoints disponíveis:
- `http://localhost:8080/actuator/health` - Status da aplicação
- `http://localhost:8080/actuator/info` - Informações

## 🧪 Testes

### Teste Unitário

```java
@SpringBootTest
public class HomeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }
}
```

## 📚 Recursos Úteis

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

## 🐛 Troubleshooting

### Problema: Templates não encontrados

**Solução**: Verifique se os arquivos estão em `src/main/resources/templates/`

### Problema: CSS/JS não carregam

**Solução**: Verifique se estão em `src/main/resources/static/`

### Problema: Porta 8080 já em uso

**Solução**: Altere em `application.properties`:
```properties
server.port=8081
```

### Problema: Erro de Thymeleaf

**Solução**: Verifique a sintaxe XML e namespaces:
```html
<html xmlns:th="http://www.thymeleaf.org">
```

## 📞 Suporte

Para dúvidas, consulte:
- `TEMPLATES_README.md` - Documentação dos templates
- `README.md` - Informações do projeto
- Documentação oficial do Spring Boot

---

**Desenvolvido com elegância para a Millésime Wine Store** 🍷
