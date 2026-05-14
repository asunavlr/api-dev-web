# Pet Shop API

API REST para um e-commerce de produtos pet, construida em **Java + Spring Boot** com persistencia em **PostgreSQL** (via Spring Data JPA). Implementa CRUD completo de categorias, produtos, clientes e pedidos, com tratamento de excecoes, validacao de entrada e documentacao OpenAPI/Swagger.

## Stack

- Java 17+
- Spring Boot 3.3
- Spring Web / Spring Data JPA / Spring Validation
- PostgreSQL 16 (driver) + H2 (perfil opcional para testes)
- springdoc-openapi 2.6 (Swagger UI)
- Maven

## Arquitetura

```
src/main/java/com/petshop/api/
├── PetShopApiApplication.java   # bootstrap
├── config/                      # OpenAPI
├── controller/                  # endpoints REST
├── service/                     # regras de negocio
├── repository/                  # Spring Data JPA
├── entity/                      # modelos JPA
├── dto/                         # objetos de transferencia + validacao
└── exception/                   # handler global + excecoes customizadas
```

### Modelo de dominio

```
Cliente ─< Pedido ─< ItemPedido >─ Produto >─ Categoria
```

- **Categoria** — agrupa produtos (Racao, Brinquedos, Higiene, Acessorios, etc.)
- **Produto** — pertence a uma categoria; tem preco, estoque e flag ativo
- **Cliente** — dados pessoais com email e CPF unicos
- **Pedido** — pertence a um cliente; possui status (`PENDENTE`, `PAGO`, `ENVIADO`, `ENTREGUE`, `CANCELADO`) e total calculado
- **ItemPedido** — linha do pedido com produto, quantidade, preco unitario congelado e subtotal

## Como executar

### Opcao 1: PostgreSQL via Docker (recomendado)

```bash
docker compose up -d
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

Ou, sem wrapper:

```bash
mvn spring-boot:run
```

### Opcao 2: PostgreSQL local

1. Crie o banco:
   ```bash
   createdb petshop
   psql petshop < database/schema.sql
   ```
2. Configure credenciais via variaveis de ambiente (ou edite `application.properties`):
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/petshop
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   mvn spring-boot:run
   ```

### Opcao 3: H2 em memoria (sem instalar Postgres)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:petshop`, user `sa`, sem senha).

A aplicacao sobe em `http://localhost:8080`.

## Documentacao da API (Swagger)

Depois de subir a aplicacao:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

## Endpoints

### Categorias — `/api/categorias`

| Metodo | Path                    | Descricao              |
|--------|-------------------------|------------------------|
| GET    | `/api/categorias`       | Lista todas            |
| GET    | `/api/categorias/{id}`  | Busca por ID           |
| POST   | `/api/categorias`       | Cria nova              |
| PUT    | `/api/categorias/{id}`  | Atualiza               |
| DELETE | `/api/categorias/{id}`  | Remove                 |

### Produtos — `/api/produtos`

| Metodo | Path                  | Descricao                                                    |
|--------|-----------------------|--------------------------------------------------------------|
| GET    | `/api/produtos`       | Lista (filtros: `?categoriaId=`, `?nome=`, `?ativo=true`)    |
| GET    | `/api/produtos/{id}`  | Busca por ID                                                 |
| POST   | `/api/produtos`       | Cria                                                         |
| PUT    | `/api/produtos/{id}`  | Atualiza                                                     |
| DELETE | `/api/produtos/{id}`  | Remove                                                       |

### Clientes — `/api/clientes`

| Metodo | Path                  | Descricao    |
|--------|-----------------------|--------------|
| GET    | `/api/clientes`       | Lista        |
| GET    | `/api/clientes/{id}`  | Busca por ID |
| POST   | `/api/clientes`       | Cadastra     |
| PUT    | `/api/clientes/{id}`  | Atualiza     |
| DELETE | `/api/clientes/{id}`  | Remove       |

### Pedidos — `/api/pedidos`

| Metodo | Path                            | Descricao                                              |
|--------|---------------------------------|--------------------------------------------------------|
| GET    | `/api/pedidos`                  | Lista todos (filtro: `?clienteId=`)                    |
| GET    | `/api/pedidos/{id}`             | Busca por ID                                           |
| POST   | `/api/pedidos`                  | Cria pedido (transacional, da baixa no estoque)        |
| PATCH  | `/api/pedidos/{id}/status`      | Atualiza status                                        |
| POST   | `/api/pedidos/{id}/cancelar`    | Cancela e devolve estoque                              |
| DELETE | `/api/pedidos/{id}`             | Remove (apenas se PENDENTE ou CANCELADO)               |

## Exemplos de payload

### Criar categoria
```http
POST /api/categorias
Content-Type: application/json

{
  "nome": "Racao",
  "descricao": "Alimentacao seca"
}
```

### Criar produto
```http
POST /api/produtos
Content-Type: application/json

{
  "nome": "Racao Premium 15kg",
  "descricao": "Racao super premium para caes adultos",
  "preco": 189.90,
  "estoque": 30,
  "ativo": true,
  "categoriaId": 1
}
```

### Criar cliente
```http
POST /api/clientes
Content-Type: application/json

{
  "nome": "Maria Silva",
  "email": "maria@example.com",
  "cpf": "12345678901",
  "telefone": "11999990001",
  "endereco": "Rua A, 100 - Sao Paulo/SP"
}
```

### Criar pedido
```http
POST /api/pedidos
Content-Type: application/json

{
  "clienteId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 3, "quantidade": 1 }
  ]
}
```

A API valida estoque, congela `precoUnitario` no momento da venda, calcula `subtotal` de cada item e o `total` do pedido automaticamente.

### Atualizar status
```http
PATCH /api/pedidos/1/status
Content-Type: application/json

{ "status": "PAGO" }
```

## Tratamento de excecoes

O `GlobalExceptionHandler` retorna respostas JSON padronizadas:

| Erro                              | Status HTTP | Quando                                |
|-----------------------------------|-------------|---------------------------------------|
| `ResourceNotFoundException`       | 404         | Recurso nao encontrado                |
| `MethodArgumentNotValidException` | 400         | Falha de validacao (Bean Validation)  |
| `BusinessException`               | 422         | Regra de negocio violada              |
| `DataIntegrityViolationException` | 409         | UK/FK violada                         |
| `Exception` (catch-all)           | 500         | Erros nao mapeados                    |

Formato:
```json
{
  "timestamp": "2026-05-14T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validacao nos dados enviados",
  "path": "/api/produtos",
  "details": ["preco: deve ser maior que zero"]
}
```

## Banco de dados

Script DDL em [`database/schema.sql`](database/schema.sql). O Hibernate tambem cria/atualiza o schema automaticamente (`spring.jpa.hibernate.ddl-auto=update`).

## Postman / Insomnia

Colecao pronta em [`postman/PetShop-API.postman_collection.json`](postman/PetShop-API.postman_collection.json). Importe no Postman ou Insomnia para testar todos os endpoints.

## Build do JAR

```bash
mvn clean package
java -jar target/petshop-api-0.0.1-SNAPSHOT.jar
```

## Licenca

MIT
