# 📦 Inventory Management System

A RESTful backend API for managing a product catalogue organised by categories. Built with **Java 17**, **Spring Boot 3.3**, **PostgreSQL 16**, and containerised with **Docker**.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 |
| ORM | Spring Data JPA (Hibernate) |
| Database | PostgreSQL 16 |
| Containerisation | Docker + Docker Compose |
| Build Tool | Maven |
| Boilerplate Reduction | Lombok |
| Validation | Jakarta Bean Validation |

---

## 📁 Project Structure

```
src/main/java/com/example/inventory/
├── category/                       ← Category feature
│   ├── Category.java               
│   ├── CategoryRepository.java     
│   ├── CategoryService.java        
│   ├── CategoryController.java     
│   └── dto/
│       ├── CategoryRequest.java    
│       └── CategoryResponse.java   
├── product/                        ← Product feature
│   ├── Product.java
│   ├── ProductRepository.java
│   ├── ProductService.java
│   ├── ProductController.java
│   └── dto/
│       ├── ProductRequest.java
│       └── ProductResponse.java
├── exception/                      ← Global error handling
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── common/                         ← Shared response models
│   └── ErrorResponse.java
└── InventoryApplication.java       ← Entry point

src/main/resources/
└── application.properties          ← DB + JPA config

docker-compose.yml                  ← PostgreSQL container
pom.xml                             ← Maven build definition
```

---

## ⚙️ Prerequisites

Make sure you have the following installed before running the project:

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) *(or use the included `./mvnw` wrapper)*
- **Docker & Docker Compose** — [Download](https://www.docker.com/products/docker-desktop/)

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/inventory-management.git
cd inventory-management
```

### 2. Start the PostgreSQL database

```bash
docker-compose up -d
```

This spins up a PostgreSQL 16 container with the following defaults:

| Setting | Value |
|---|---|
| Database | `inventory` |
| Username | `admin` |
| Password | `secret` |
| Port | `5432` |

> Data is persisted in a named Docker volume (`postgres_data`) and survives container restarts.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at **`http://localhost:8080`**.

On first startup, Hibernate automatically creates the `categories` and `products` tables in PostgreSQL.

---

## 📡 API Reference

### Categories

#### Create a category
```http
POST /api/categories
Content-Type: application/json

{
  "name": "Electronics"
}
```

**Response — 201 Created**
```json
{
  "id": 1,
  "name": "Electronics",
  "productCount": 0
}
```

---

#### List all categories
```http
GET /api/categories
```

---

#### Get a category by ID
```http
GET /api/categories/{id}
```

---

### Products

#### Create a product
```http
POST /api/products?categoryId=1
Content-Type: application/json

{
  "name": "Laptop",
  "price": 999.99,
  "quantity": 10
}
```

**Response — 201 Created**
```json
{
  "id": 1,
  "name": "Laptop",
  "price": 999.99,
  "quantity": 10,
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

---

#### List all products
```http
GET /api/products
```

---

#### Get a product by ID
```http
GET /api/products/{id}
```

---

#### Get products by category
```http
GET /api/products/by-category/{categoryId}
```

---

## ❌ Error Responses

All errors return a consistent JSON payload:

```json
{
  "timestamp": "2026-05-17T10:23:00",
  "status": 404,
  "message": "Category not found with id: 99",
  "path": "/api/products"
}
```

| Status | Trigger |
|---|---|
| `400 Bad Request` | A required field is missing or fails validation |
| `404 Not Found` | The requested resource ID does not exist |
| `500 Internal Server Error` | An unexpected server-side error occurred |

---

## ✅ Validation Rules

| Field | Rule |
|---|---|
| `category.name` | Required, cannot be blank |
| `product.name` | Required, cannot be blank |
| `product.price` | Required, must be `≥ 0` |
| `product.quantity` | Required, must be `≥ 0` |

---

## 🧪 Quick Test with curl

```bash
# 1. Create a category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics"}'

# 2. Create a product linked to category ID 1
curl -X POST "http://localhost:8080/api/products?categoryId=1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":999.99,"quantity":10}'

# 3. List all products
curl http://localhost:8080/api/products

# 4. Get products by category
curl http://localhost:8080/api/products/by-category/1

# 5. Trigger a 404 — invalid category ID
curl -X POST "http://localhost:8080/api/products?categoryId=999" \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":999.99,"quantity":10}'

# 6. Trigger a 400 — blank category name
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":""}'
```

---

## 🗄️ Database Schema

```sql
-- Categories table
CREATE TABLE categories (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE
);

-- Products table
CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    quantity    INTEGER NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id)
);
```

> Hibernate generates these tables automatically on startup via `spring.jpa.hibernate.ddl-auto=update`.

---

## 🔧 Configuration

All configuration lives in `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory
spring.datasource.username=admin
spring.datasource.password=secret
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
```

---

## 🛑 Stopping the Application

```bash
# Stop Spring Boot
Ctrl + C

# Stop and remove the Docker container (data is preserved in the volume)
docker-compose down

# Stop and remove everything including the database volume
docker-compose down -v
```
