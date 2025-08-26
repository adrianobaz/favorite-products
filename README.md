# API Produtos Favoritos

## Tecnologias utilizadas

- Kotlin
- Spring Boot
- Postgres
- Redis
- Nginx

## Arquitetura

Cosiderando que a aplicação será consumida tanto por clientes externos quanto por sistemas internos e terá alto volume uso um load balancer (Nginx) nôs auxilia a lidar de maneira controlada o trafego externo.
Para a integração necessária com a API externa de produtos utilizar uma estratégia de caching, nesse caso aside auxilia em reduzir a carga do banco de dados relacional para as operações que seriam repetitivas no mesmo.

A autenticação e autorização considera, a partir de um token seguro (JWT) emitido pela aplicação resulte em acesso a recursos somente para aquele usuário, ou seja só quem gerou aquela token pode utilizalo.

![Arch](./arch.png "Desenho Arquitetural")

## Organização do projeto

O projeto foi organizado utilizando como inspiração uma adaptação tanto do padrão Ports and Adapters quanto de Domain Driven Design, no qual a camada do núcleo da aplicação deve ficar completamente isolada de detalhes de tecnologia, elevando assim a testabilidade, extensibilidade e abertura para novas funcionalidades. Ex:

## Visão geral da API e como Consumir

 >**OBS: É possível acessar a documentação completa dos endpoints disponível em [link](http://localhost:8080/docs) quando a aplicação estiver em execução**

> Há um arquivo de [collection](./collection/favorite_products_collection.json) do [Postman](https://www.postman.com/) contendo os endpoints já configurados para teste.

API responsável por prover operações de cadastro, atualização, busca e deleção de clientes. Assim como autenticação, autorização e cadastro de produtos favoritos para os clientes previamente existentes

**Base URL:** `http://localhost:8080`

**Autenticação:** Bearer Token (JWT) no header `Authorization`

## Endpoints

### 1. Criar Cliente

Cria um novo cliente no sistema.

**Endpoint:** `POST /v1/clients`

**Headers:**
```
Content-Type: application/json
Accept: application/json
```

**Corpo da Requisição:**
| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| name | string | Sim | Nome completo (min: 2, max: 100) |
| email | string | Sim | Email válido e único |

**Exemplo de Requisição:**
```bash
curl --request POST \
  --url http://localhost:8080/v1/clients \
  --header 'accept: application/json' \
  --header 'content-type: application/json' \
  --data '{
  "name": "Myra Hodkiewicz",
  "email": "Kasandra10@yahoo.com"
}'
```

**Resposta de Sucesso (201 Created):**
```json
{
  "external_id": "7e642275-9e6c-4e2c-8352-b55aaba2db00",
  "name": "Allan Mosciski",
  "email": "Kasandra10@yahoo.com"
}
```

### 2. Gerar token (AUTENTICAÇÃO)

Gerar token para autenticação/autorização.

**Endpoint:** `POST /auth/token/request`

**Headers:**
```
Content-Type: application/json
```

**Corpo da Requisição:**
| Parâmetro | Tipo | Obrigatório | Descrição | Padrão |
|-----------|------|-------------|-----------|---------|
| external_id | string | Sim | ID externo do cliente | 1 |

**Exemplo de Requisição:**
```bash
curl --request POST \
  --url http://localhost:8080/auth/token/request \
  --header 'accept: application/json' \
  --header 'content-type: application/json' \
  --data '{
  "external_id": "7e642275-9e6c-4e2c-8352-b55aaba2db00"
}'
```

**Resposta de Sucesso (303 SEE_OTHER):**

**Headers:**
```
Content-Type: application/json
Location: http://localhost:8080/auth/token/consume?token=3458ecad-1497-42e5-a490-0e9d1ec3e57c
```

### 3. Consumir token e obter JWT

Consome o token do link e retorna um JWT.

**Endpoint:** `GET /auth/token/consume`

**Headers:**
```
Content-Type: application/json
```

**Parâmetros de Query:**
| Parâmetro | Tipo | Obrigatório | Descrição | Padrão |
|-----------|------|-------------|-----------|---------|
| token | string | Sim | Token gerado pelo sistema | UUIv4 |

**Exemplo de Requisição:**
```bash
curl --request GET \
  --url 'http://localhost:8080/auth/token/consume?token=3458ecad-1497-42e5-a490-0e9d1ec3e57c' \
  --header 'accept: application/json'
```

**Resposta de Sucesso (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3ZTY0MjI3NS05ZTZjLTRlMmMtODM1Mi1iNTVhYWJhMmRiMDAiLCJpc3MiOiJmYXZvcml0ZS1wcm9kdWN0cyIsImlhdCI6MTc1NjE3NTk2NiwiZXhwIjoxNzU2MTc3NzY2LCJlbWFpbCI6IldhdHNvbjcwQGhvdG1haWwuY29tIiwibmFtZSI6IkFsbGFuIE1vc2Npc2tpIiwicm9sZXMiOiJDTElFTlQifQ.gQhckXh-OQN3mKni-uT6EH2bxgiyeXkBlT3X6mcUOJo",
  "expires_in_seconds": 1800
}
```

### 4. Atualizar cliente

Atualiza os dados do cliente.

**Endpoint:** `PATCH /v1/clients/{external_id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parâmetros de Path:**
| Parâmetro | Tipo | Descrição | Padrão
|-----------|------|-----------|-------|
| external_id | string | ID único do usuário | UUIDv4 |

**Corpo da Requisição:**
| Parâmetro | Tipo | Obrigatório | Descrição | Padrão |
|-----------|------|-------------|-----------|---------|
| email | string | Não | Email unico | Email |
| name | string | Não | Nome para ser atualizado | Arthur |

**Exemplo de Requisição:**
```bash
curl --request PATCH \
  --url http://localhost:8080/v1/clients/7e642275-9e6c-4e2c-8352-b55aaba2db00 \
  --header 'accept: application/json' \
  --header 'authorization: Bearer <JWT>' \
  --header 'content-type: application/json' \
  --data '{
  "email": "Edna_Stark@gmail.com"
}'
```

**Resposta de Sucesso (200 OK):**
```json
{
  "external_id": "7e642275-9e6c-4e2c-8352-b55aaba2db00",
  "name": "Allan Mosciski",
  "email": "Skyla.Kreiger98@hotmail.com"
}
```

### 5. Buscar cliente

Atualiza os dados do cliente.

**Endpoint:** `GET /v1/clients/{external_id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parâmetros de Path:**
| Parâmetro | Tipo | Descrição | Padrão
|-----------|------|-----------|-------|
| external_id | string | ID único do usuário | UUIDv4 |

**Exemplo de Requisição:**
```bash
curl --request GET \
  --url http://localhost:8080/v1/clients/7e642275-9e6c-4e2c-8352-b55aaba2db00 \
  --header 'accept: application/json' \
  --header 'authorization: Bearer <JWT>'
```

**Resposta de Sucesso (200 OK):**
```json
{
  "external_id": "7e642275-9e6c-4e2c-8352-b55aaba2db00",
  "name": "Allan Mosciski",
  "email": "Skyla.Kreiger98@hotmail.com"
}
```

### 6. Adicionar produtos aos favoritos do cliente

Adiciona produtos a lista de favoritos do cliente

**Endpoint:** `POST /v1/clients/{external_id}/favorite-products`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parâmetros de Path:**
| Parâmetro | Tipo | Descrição | Padrão |
|-----------|------|-----------|-------|
| external_id | string | ID único do usuário | UUIDv4 |

**Corpo da Requisição:**
| Parâmetro | Tipo | Obrigatório | Descrição | Padrão |
|-----------|------|-------------|-----------|---------|
| product_ids | list | Sim | ID dos produtos | [1,2,3,4] |

**Exemplo de Requisição:**
```bash
curl --request POST \
  --url http://localhost:8080/v1/clients/7e642275-9e6c-4e2c-8352-b55aaba2db00/favorite-products \
  --header 'accept: application/json' \
  --header 'authorization: Bearer <JWT>' \
  --header 'content-type: application/json' \
  --data '{
  "product_ids": [
    20,
    1,
    5
  ]
}'
```

**Resposta de Sucesso (200 OK):**
```json
[
  {
    "id": 20,
    "title": "DANVOUY Womens T Shirt Casual Cotton Short",
    "image": "https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_t.png",
    "price": 12.99,
    "rating": {
      "rate": 3.6,
      "count": 145
    }
  },
  {
    "id": 1,
    "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
    "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png",
    "price": 109.95,
    "rating": {
      "rate": 3.9,
      "count": 120
    }
  },
  {
    "id": 5,
    "title": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
    "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_t.png",
    "price": 695,
    "rating": {
      "rate": 4.6,
      "count": 400
    }
  }
]
```

### 7. Buscar produtos favoritos

Busca o(s) produto(s) favorito(s) de um cliente

**Endpoint:** `GET /v1/clients/{external_id}/favorite-products`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parâmetros de Path:**
| Parâmetro | Tipo | Descrição | Padrão |
|-----------|------|-----------|------|
| external_id | string | ID único do usuário | UUIDv4 |

**Exemplo de Requisição:**
```bash
curl --request GET \
  --url http://localhost:8080/v1/clients/7e642275-9e6c-4e2c-8352-b55aaba2db00/favorite-products \
  --header 'accept: application/json' \
  --header 'authorization: Bearer <JWT>'
```

**Resposta de Sucesso (200 OK):**
```json
[
  {
    "id": 20,
    "title": "DANVOUY Womens T Shirt Casual Cotton Short",
    "image": "https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_t.png",
    "price": 12.99,
    "rating": {
      "rate": 3.6,
      "count": 145
    }
  },
  {
    "id": 1,
    "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
    "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png",
    "price": 109.95,
    "rating": {
      "rate": 3.9,
      "count": 120
    }
  },
  {
    "id": 5,
    "title": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
    "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_t.png",
    "price": 695,
    "rating": {
      "rate": 4.6,
      "count": 400
    }
  }
]
```

### 8. Deleta um cliente

Exclui o cliente.

**Endpoint:** `DELETE /v1/clients/{external_id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parâmetros de Path:**
| Parâmetro | Tipo | Descrição | Padrão |
|-----------|------|-----------|--------|
| external_id | string | ID único do cliente | UUIDv4 |

**Exemplo de Requisição:**
```bash
curl --request DELETE \
  --url http://localhost:8080/v1/clients/7e642275-9e6c-4e2c-8352-b55aaba2db00 \
  --header 'authorization: Bearer <JWT>'
```

**Resposta de Sucesso (204 NO CONTENT):**

## Códigos de Erro

### Códigos HTTP Padrão

| Código | Descrição |
|--------|-----------|
| 200 | Requisição bem-sucedida |
| 201 | Recurso criado com sucesso |
| 400 | Dados da requisição inválidos |
| 401 | Token de autenticação inválido ou ausente |
| 403 | Permissão negada |
| 404 | Recurso não encontrado |
| 422 | Erro de validação |
| 500 | Erro interno do servidor |

### Exemplos de Respostas de Erro

**Erro de Validação (422):**
```json
{
  "type": "about:blank",
  "title": "Violação de regra de negócio",
  "status": 422,
  "detail": "Customer already registered with this email",
  "instance": "/v1/clients/75606e19-8a87-4e1c-85e9-ab8f8927310f",
  "properties": {
    "errorCode": "EMAIL_ALREADY_REGISTERED",
    "timestamp": "2025-08-25T17:43:22.345707Z"
  }
}
```

**Recurso Não Encontrado (404):**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Not Found",
  "instance": "/v1/clients/75606e19-8a87-4e1c-85e9-ab8f8927310f",
}
```

**Token Inválido (403):**
```json
{
  "type": "about:blank",
  "title": "Ação não autorizada",
  "status": 403,
  "detail": "Unauthorized action: Invalid or expired token",
  "instance": "/auth/token/consume",
  "properties": {
    "action": "Invalid or expired token",
    "timestamp": "2025-08-26T04:06:34.454634208Z"
  }
}
```

### Como executar a aplicação utilizando Docker

Com o [Docker](https://docs.docker.com/get-started/get-docker/) instalado, é possível executar a aplicação independente de quaisquer características particulares de cada sistema operacional.

Para executar todos os componentes necessários da API basta executar o seguinte comando na pasta raiz do projeto:

```bash
docker-compose up -d
```

ou

```bash
docker compose up -d
```