# Millésime - Templates HTML/Thymeleaf

## 📋 Visão Geral

Este projeto contém templates HTML/Thymeleaf para a loja de vinhos premium **Millésime**, desenvolvidos com um design clássico neoclássico elegante. Os templates mantêm a estrutura Java do projeto Spring Boot enquanto implementam uma interface visual sofisticada e refinada.

## 🎨 Design System

### Paleta de Cores
- **Primário (Borgonha)**: `#5C2E3A` - Cor principal para textos e acentos
- **Acentos (Ouro)**: `#D4AF37` - Detalhes de luxo e refinamento
- **Secundário (Bege Quente)**: `#F5E6D3` - Backgrounds sofisticados
- **Muted (Cinza Claro)**: `#E8E0D5` - Elementos sutis
- **Background**: `#FEFDFB` - Fundo claro e elegante
- **Foreground**: `#1A1A1A` - Texto principal

### Tipografia
- **Headings**: Playfair Display (serif) - Elegância e sofisticação
- **Body**: Open Sans (sans-serif) - Legibilidade e modernidade

### Componentes Principais
- **Buttons**: `.btn-premium`, `.btn-premium-outline`, `.btn-premium-gold`
- **Cards**: `.card-elegant` - Cartões com bordas sutis e efeitos hover
- **Badges**: `.badge-elegant` - Distintivos com estilo sofisticado
- **Dividers**: `.divider-gold` - Linhas decorativas em gradiente
- **Sections**: `.section` - Espaçamento generoso entre seções

## 📁 Estrutura de Arquivos

```
Millesime_Final/
├── src/main/resources/
│   ├── templates/
│   │   ├── index.html          # Página inicial
│   │   ├── catalog.html        # Catálogo de vinhos
│   │   ├── product.html        # Detalhes do produto
│   │   ├── layout.html         # Template base (não usado diretamente)
│   │   ├── cart.html           # Carrinho de compras
│   │   └── checkout.html       # Checkout
│   └── static/
│       ├── css/
│       │   └── style.css       # Estilos principais
│       └── js/
│           └── main.js         # Funcionalidades JavaScript
```

## 🔧 Templates Disponíveis

### 1. **index.html** - Página Inicial
Apresenta a loja com:
- **Hero Section**: Banner grande com chamada para ação
- **Featured Wines**: Destaques da casa em grid responsivo
- **Categories**: Categorias de vinhos
- **Sommelier Pick**: Recomendação especial do sommelier
- **Pairings**: Harmonizações de vinhos com alimentos
- **Footer**: Informações de contato e links úteis

**Variáveis Thymeleaf esperadas:**
```html
${cartCount}          <!-- Quantidade de itens no carrinho -->
${featuredWines}      <!-- Lista de vinhos em destaque -->
${categories}         <!-- Categorias de vinhos -->
${sommelierPick}      <!-- Vinho recomendado -->
${pairings}           <!-- Harmonizações -->
```

### 2. **catalog.html** - Catálogo de Vinhos
Exibe lista de produtos com:
- **Filtros Laterais**: Tipo, preço, região, avaliação
- **Grid de Produtos**: Cards com imagens e preços
- **Ordenação**: Opções de ordenação (relevância, preço, avaliação)
- **Paginação**: Navegação entre páginas

**Variáveis Thymeleaf esperadas:**
```html
${wines}              <!-- Lista de vinhos -->
${totalCount}         <!-- Total de produtos -->
${currentPage}        <!-- Página atual -->
${totalPages}         <!-- Total de páginas -->
${filters}            <!-- Filtros aplicados -->
```

### 3. **product.html** - Detalhes do Produto
Página completa do produto com:
- **Galeria de Imagens**: Imagem principal com thumbnails
- **Informações**: Tipo, uva, safra, álcool
- **Preço e Estoque**: Preço formatado e status de disponibilidade
- **Adicionar ao Carrinho**: Seletor de quantidade
- **Abas**: Descrição, notas de degustação, harmonização, avaliações
- **Produtos Relacionados**: Sugestões de outros vinhos

**Variáveis Thymeleaf esperadas:**
```html
${product}            <!-- Objeto do produto
  - id
  - name
  - price
  - region
  - country
  - type
  - grape
  - harvest
  - stock
  - rating
  - reviews
  - description
  - tastingNotes
  - pairing
  - images[]
-->
${relatedProducts}    <!-- Produtos relacionados -->
```

### 4. **layout.html** - Template Base
Template pai com estrutura comum:
- **Header**: Navegação, logo, carrinho
- **Main**: Placeholder para conteúdo
- **Footer**: Rodapé com informações

Pode ser estendido usando Thymeleaf fragments.

### 5. **cart.html** - Carrinho de Compras
Página do carrinho com:
- **Lista de Itens**: Produtos adicionados
- **Quantidade**: Ajuste de quantidade
- **Subtotal**: Cálculo de preços
- **Cupons**: Aplicação de códigos promocionais
- **Checkout**: Botão para finalizar compra

**Variáveis Thymeleaf esperadas:**
```html
${cartItems}          <!-- Itens do carrinho -->
${subtotal}           <!-- Subtotal -->
${tax}                <!-- Impostos -->
${total}              <!-- Total -->
${coupon}             <!-- Cupom aplicado -->
```

### 6. **checkout.html** - Checkout
Página de finalização com:
- **Resumo do Pedido**: Itens e valores
- **Dados de Entrega**: Endereço e opções de envio
- **Dados de Pagamento**: Formas de pagamento
- **Confirmação**: Botão para confirmar pedido

**Variáveis Thymeleaf esperadas:**
```html
${order}              <!-- Dados do pedido -->
${shippingOptions}    <!-- Opções de envio -->
${paymentMethods}     <!-- Formas de pagamento -->
${user}               <!-- Dados do usuário -->
```

## 🎯 Integração com Spring Boot

### Controlador Exemplo

```java
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("cartCount", 3);
        model.addAttribute("featuredWines", wineService.getFeaturedWines());
        model.addAttribute("categories", wineService.getCategories());
        return "index";
    }
    
    @GetMapping("/catalogo")
    public String catalog(Model model, 
                         @RequestParam(defaultValue = "1") int page) {
        model.addAttribute("wines", wineService.getWines(page));
        model.addAttribute("totalCount", wineService.getTotalCount());
        model.addAttribute("currentPage", page);
        return "catalog";
    }
    
    @GetMapping("/produto/{id}")
    public String product(@PathVariable Long id, Model model) {
        model.addAttribute("product", wineService.getWineById(id));
        model.addAttribute("relatedProducts", wineService.getRelatedWines(id));
        return "product";
    }
}
```

## 🎨 Customização de Estilos

### Variáveis CSS Personalizáveis

Edite `/static/css/style.css` para alterar cores:

```css
:root {
    --primary: #5C2E3A;           /* Cor primária */
    --accent: #D4AF37;            /* Cor de acentos */
    --secondary: #F5E6D3;         /* Cor secundária */
    --background: #FEFDFB;        /* Fundo */
    --foreground: #1A1A1A;        /* Texto */
}
```

### Classes Utilitárias

```html
<!-- Flexbox -->
<div class="flex items-center justify-between gap-4">

<!-- Grid -->
<div class="grid" style="grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));">

<!-- Spacing -->
<div class="mb-4 mt-auto pt-4">

<!-- Typography -->
<h1>Título Principal</h1>
<h2>Subtítulo</h2>
<p>Parágrafo</p>

<!-- Buttons -->
<button class="btn-premium">Botão Primário</button>
<button class="btn-premium-outline">Botão Outline</button>
<button class="btn-premium-gold">Botão Gold</button>

<!-- Badges -->
<span class="badge-elegant">Destaque</span>

<!-- Cards -->
<div class="card-elegant">Conteúdo</div>
```

## 📱 Responsividade

Todos os templates são totalmente responsivos com breakpoints:
- **Mobile**: < 640px
- **Tablet**: 640px - 1024px
- **Desktop**: > 1024px

## ⚡ Performance

- CSS otimizado e minificado
- JavaScript assíncrono
- Imagens otimizadas
- Lazy loading habilitado
- Cache de recursos estáticos

## 🔐 Segurança

- Proteção contra XSS com Thymeleaf
- CSRF tokens em formulários
- Validação de entrada no servidor
- Sanitização de dados

## 📚 Recursos Adicionais

### Google Fonts
- Playfair Display: Headings elegantes
- Open Sans: Texto corpo legível

### Ícones SVG
Ícones inline para melhor performance

### Animações CSS
- `fadeInUp`: Entrada suave
- `slideInLeft`: Deslizamento lateral
- `goldGlow`: Brilho dourado

## 🚀 Deploy

1. Compile o projeto Maven:
```bash
mvn clean package
```

2. Execute a aplicação:
```bash
java -jar target/Millesime-0.0.1-SNAPSHOT.jar
```

3. Acesse em: `http://localhost:8080`

## 📝 Notas Importantes

- Todos os templates usam Thymeleaf como template engine
- Imagens são placeholders e devem ser substituídas por URLs reais
- Preços são formatados em Real Brasileiro (R$)
- Datas usam formato brasileiro (DD/MM/YYYY)
- Suporte a múltiplos idiomas pode ser adicionado

## 🎓 Estrutura de Dados Esperada

### Objeto Wine
```java
{
    id: Long,
    name: String,
    price: BigDecimal,
    region: String,
    country: String,
    type: String,           // Tinto, Branco, Rosé, Espumante
    grape: String,          // Variedade de uva
    harvest: Integer,       // Ano da safra
    stock: Integer,
    rating: Double,         // 0-5
    reviews: Integer,
    description: String,
    tastingNotes: String,
    pairing: String,
    image: String,          // URL da imagem
    specialHarvest: Boolean
}
```

## 📞 Suporte

Para dúvidas ou sugestões sobre os templates, consulte a documentação do Thymeleaf:
https://www.thymeleaf.org/

## 📄 Licença

Todos os templates são parte do projeto Millésime e seguem a mesma licença do projeto principal.

---

**Desenvolvido com elegância e sofisticação para a Millésime Wine Store** 🍷
