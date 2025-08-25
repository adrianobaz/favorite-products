# Produtos Favoritos API

O aiqfome está expandindo seus canais de integração e precisa de uma API robusta para gerenciar os "produtos favoritos" de usuários na plataforma.
**Essa funcionalidade será usada por apps e interfaces web para armazenar e consultar produtos marcados como favoritos pelos clientes. A API terá alto volume de uso e integrará com outros sistemas internos e externos.**

Clientes

- Criar, visualizar, editar e remover clientes.
- Dados obrigatórios: nome e e-mail.
- Um mesmo e-mail não pode se repetir no cadastro.

(POST, GET, PATCH e DELETE /v1/clients)

Favoritos

- Um cliente deve ter uma lista de produtos favoritos.
- Os produtos devem ser validados via API externa (link fornecido abaixo).
- Um produto não pode ser duplicado na lista de um cliente.
- Produtos favoritos devem exibir: ID, título, imagem, preço e review (se houver).

GET v1/clients/{id | email}/favorite_products
PATCH /v1/clients/{id | email}/favorite_products
DELETE /v1/clients/{id | email}/favorite_products/{id}

# Tecnologias externas

- Load Balancer (HAProxy/Nginx)
- Banco de dados relacional: Postgre
- Banco de dados não relacional: Redis? (caso leitura seja maior que escrita)
- Docker/Docker-compose


## Utilizando Golang*:

sqlc (database)
chi - net/http (routing/client http)
jwt / email validation (authentication/authorization)
tern (migration)
testcontainers (integration test)
decimal (amount)
godotenv
go-redis (cache?)

## Utilizando Kotlin

Spring (Web/MVC)
jackson
JPA
restassured
mockk
open-feign
flyway
redis/unlogged tables(sessão autenticação/autorização, validar email)

---

## ✅ **1. Role-Based Access Control (RBAC)**

## ✅ **2. Email Verification Flow**

We’ll continue with:

* 🔄 `Chi` for routing
* 🔐 `JWT` for session and authorization
* 🗃 `GORM + SQLite` for user storage
* 📧 Simulated email verification via **email tokens**

    * (we’ll print the "magic link" to console — easy to plug in real email later)

---

# 🏗 System Overview

### ✅ Auth Flow:

1. User submits `name` and `email`
2. System generates a **one-time email verification token** (JWT or UUID) and "sends" it via email (print to console)
3. User clicks verification link `/verify?token=...`
4. User is verified, and you issue a **login JWT** with embedded `role` (e.g., `"user"`)

---

## ✍️ Step-by-Step Code

---

### 🧩 1. Add Role & Verified Fields to `User`

```go
type User struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Name      string    `json:"name"`
	Email     string    `gorm:"unique" json:"email"`
	Role      string    `gorm:"default:user" json:"role"`
	Verified  bool      `gorm:"default:false" json:"verified"`
	CreatedAt time.Time
}
```

Run this once to auto-migrate with GORM:

```go
db.AutoMigrate(&User{})
```

---

### 🔐 2. JWT Claims (Add Role)

```go
type Claims struct {
	Name     string `json:"name"`
	Email    string `json:"email"`
	Role     string `json:"role"`
	Verified bool   `json:"verified"`
	jwt.RegisteredClaims
}
```

---

### 📮 3. Email Verification Handler

```go
func AuthHandler(w http.ResponseWriter, r *http.Request) {
	var input User
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		http.Error(w, "Invalid request", http.StatusBadRequest)
		return
	}

	if input.Name == "" || input.Email == "" {
		http.Error(w, "Name and email required", http.StatusBadRequest)
		return
	}

	var user User
	result := db.First(&user, "email = ?", input.Email)
	if result.Error == gorm.ErrRecordNotFound {
		user = User{Name: input.Name, Email: input.Email}
		db.Create(&user)
	} else if result.Error != nil {
		http.Error(w, "Database error", http.StatusInternalServerError)
		return
	}

	// Create email verification token (short-lived)
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"email": user.Email,
		"exp":   time.Now().Add(15 * time.Minute).Unix(),
	})
	tokenStr, _ := token.SignedString(jwtSecret)

	// Print simulated verification link
	log.Printf("Verify email: http://localhost:8080/verify?token=%s\n", tokenStr)

	json.NewEncoder(w).Encode(map[string]string{
		"message": "Check your email to verify your account (simulated)",
	})
}
```

---

### ✅ 4. Email Verification Endpoint

```go
func VerifyHandler(w http.ResponseWriter, r *http.Request) {
	tokenStr := r.URL.Query().Get("token")
	if tokenStr == "" {
		http.Error(w, "Missing token", http.StatusBadRequest)
		return
	}

	token, err := jwt.Parse(tokenStr, func(token *jwt.Token) (interface{}, error) {
		return jwtSecret, nil
	})
	if err != nil || !token.Valid {
		http.Error(w, "Invalid token", http.StatusUnauthorized)
		return
	}

	claims := token.Claims.(jwt.MapClaims)
	email := claims["email"].(string)

	var user User
	if err := db.First(&user, "email = ?", email).Error; err != nil {
		http.Error(w, "User not found", http.StatusNotFound)
		return
	}

	user.Verified = true
	db.Save(&user)

	// Generate login token
	loginClaims := &Claims{
		Name:     user.Name,
		Email:    user.Email,
		Role:     user.Role,
		Verified: true,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}

	loginToken := jwt.NewWithClaims(jwt.SigningMethodHS256, loginClaims)
	signedToken, _ := loginToken.SignedString(jwtSecret)

	json.NewEncoder(w).Encode(map[string]string{
		"token": signedToken,
	})
}
```

---

### 🛡 5. Auth Middleware (Check Verified + Add Role-Based Guard)

```go
func AuthMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		auth := r.Header.Get("Authorization")
		if auth == "" || !strings.HasPrefix(auth, "Bearer ") {
			http.Error(w, "Missing token", http.StatusUnauthorized)
			return
		}
		tokenStr := strings.TrimPrefix(auth, "Bearer ")

		claims := &Claims{}
		token, err := jwt.ParseWithClaims(tokenStr, claims, func(token *jwt.Token) (interface{}, error) {
			return jwtSecret, nil
		})
		if err != nil || !token.Valid {
			http.Error(w, "Invalid token", http.StatusUnauthorized)
			return
		}

		if !claims.Verified {
			http.Error(w, "Email not verified", http.StatusForbidden)
			return
		}

		ctx := context.WithValue(r.Context(), "user", claims)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}
```

---

### 🔐 6. Role Middleware (Optional)

```go
func RoleMiddleware(requiredRole string) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			claims, ok := r.Context().Value("user").(*Claims)
			if !ok || claims.Role != requiredRole {
				http.Error(w, "Forbidden", http.StatusForbidden)
				return
			}
			next.ServeHTTP(w, r)
		})
	}
}
```

---

### 👤 7. Protected Endpoints

```go
func MeHandler(w http.ResponseWriter, r *http.Request) {
	claims := r.Context().Value("user").(*Claims)
	json.NewEncoder(w).Encode(map[string]string{
		"name":     claims.Name,
		"email":    claims.Email,
		"role":     claims.Role,
		"verified": fmt.Sprintf("%v", claims.Verified),
	})
}

func AdminOnly(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintln(w, "Welcome, Admin!")
}
```

---

### 🛣️ 8. Register Routes

```go
r.Post("/auth", AuthHandler)
r.Get("/verify", VerifyHandler)

r.Group(func(r chi.Router) {
	r.Use(AuthMiddleware)
	r.Get("/me", MeHandler)

	r.With(RoleMiddleware("admin")).Get("/admin", AdminOnly)
})
```

---

## 🧪 Test Flow

1. Request:

   ```bash
   curl -X POST http://localhost:8080/auth \
     -H "Content-Type: application/json" \
     -d '{"name": "Alice", "email": "alice@example.com"}'
   ```

2. Copy verification link from console:

   ```
   Verify email: http://localhost:8080/verify?token=...
   ```

3. Call `/verify?token=...`:

   ```bash
   curl "http://localhost:8080/verify?token=..."
   ```

4. Use returned JWT to call `/me`:

   ```bash
   curl http://localhost:8080/me \
     -H "Authorization: Bearer <TOKEN>"
   ```

---

## ✅ Summary

| Feature               | Status |
| --------------------- | ------ |
| Email verification    | ✅      |
| JWT auth              | ✅      |
| DB storage (GORM)     | ✅      |
| Role-based access     | ✅      |
| Middleware-based RBAC | ✅      |

---

Would you like to:

* 🔧 Hook up real email sending (SMTP or SendGrid)?
* 💾 Use PostgreSQL or another DB?
* 🔐 Add refresh tokens?

Let me know what’s next.


---

📅 Contexto
O aiqfome está expandindo seus canais de integração e precisa de uma API robusta para gerenciar os "produtos favoritos" de usuários na plataforma.
Essa funcionalidade será usada por apps e interfaces web para armazenar e consultar produtos marcados como favoritos pelos clientes. A API terá alto volume de uso e integrará com outros sistemas internos e externos.

🔧 Sua missão:
Desenvolva uma API RESTful que permita:
Clientes

Criar, visualizar, editar e remover clientes.
Dados obrigatórios: nome e e-mail.
Um mesmo e-mail não pode se repetir no cadastro.
Favoritos

Um cliente deve ter uma lista de produtos favoritos.
Os produtos devem ser validados via API externa (link fornecido abaixo).
Um produto não pode ser duplicado na lista de um cliente.
Produtos favoritos devem exibir: ID, título, imagem, preço e review (se houver).
Requisitos de Integração

Sugerimos o uso de uma API genérica para buscar produtos. Porém, para facilitar a execução e deixar tudo mais direto ao ponto, recomendamos o uso da seguinte API pública:

🔗 https://fakestoreapi.com/docs

Você pode utilizar especificamente estes dois endpoints:

Listar todos os produtos:
GET https://fakestoreapi.com/products

Buscar produto por ID:
GET https://fakestoreapi.com/products/{id}

Confira algumas dicas aqui
⚖️ Regras Gerais

A API deve ser pública, mas conter autenticação e autorização.
Evite duplicidade de dados.
Estruture bem o código, seguindo boas práticas REST.
Pense em performance e escalabilidade.
Documente sua API (OpenAPI/Swagger é bem-vindo, mas opcional).
Não use IA ou cópias. Será passível de eliminação.

💡 Requisitos Técnicos
Você pode escolher uma das seguintes linguagens:

Go
Python
PHP
Node.js (Javascript ou Typescript)
Ou outra linguagem (sem apego!)
Banco de dados sugerido:

PostgreSQL (preferencial)
MySQL
MongoDB
📊 O que esperamos:

Critério	Peso
Correção e funcionamento da API	🔥🔥🔥🔥
Modelagem de dados (clientes/produtos)	🔥🔥🔥
Validação e controle de dados	🔥🔥🔥
Documentação ou instrução de uso	🔥🔥
Segurança básica (auth, validação)	🔥🔥

🖇️ Entrega

Este desafio deve ser entregue em até 5 dias corridos a partir do recebimento deste documento.
Suba em um repositório público (GitHub, GitLab, Bitbucket) OU envie um ZIP para natalia.neto@aiqfome.com ou (44) 8812-0687
Inclua instruções claras de como rodar o projeto.
“Leia-me” é bem-vindo: explique suas escolhas.
Boa sorte, e mostre sua identidade no código ❤
