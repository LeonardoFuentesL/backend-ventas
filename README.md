# Backend: Microservicio de Productos y Ventas

Microservicio REST desarrollado con **Spring Boot 4.1.2** y **Java 21** para la gestión del catálogo de productos y el registro de ventas con control transaccional de inventario sobre **PostgreSQL**.

- **Repositorio Frontend:** [https://github.com/LeonardoFuentesL/frontend-ventas](https://github.com/LeonardoFuentesL/frontend-ventas)

## Requisitos previos

- **JDK 21** (`javac -version` en versión `21.x`)
- **PostgreSQL 18** activo en `localhost:5432`
- **Maven Wrapper** incluido en el repositorio (`./mvnw`)

## 1. Configuración de la base de datos (`ventas_db`)

Crea el usuario y la base de datos en PostgreSQL:

```sql
CREATE USER ventas_user WITH PASSWORD '<tu_contraseña>';
CREATE DATABASE ventas_db OWNER ventas_user;
```

Ejecuta los scripts SQL para crear las tablas (`productos`, `ventas`, `detalle_venta`) y cargar los 5 productos iniciales:

```bash
psql -U ventas_user -d ventas_db -h localhost -f sql/schema.sql
psql -U ventas_user -d ventas_db -h localhost -f sql/data.sql
```

Configura tus credenciales locales en `src/main/resources/application.yaml` (puerto HTTP `8000`, base de datos `jdbc:postgresql://localhost:5432/ventas_db`, usuario `ventas_user`).

## 2. Arranque del microservicio

```bash
./mvnw spring-boot:run
```

Rutas disponibles al iniciar:

- **API REST Productos:** `http://localhost:8000/api/productos`
- **API REST Ventas:** `http://localhost:8000/api/ventas`
- **Swagger UI:** `http://localhost:8000/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8000/v3/api-docs`
- **Health Check:** `http://localhost:8000/actuator/health`

## 3. Ejecución de pruebas automatizadas

Las pruebas de integración usan `MockMvc` y `@Transactional` para hacer rollback automático al terminar cada caso y no alterar la base de datos:

```bash
./mvnw clean test
```

> **Nota:** Se recomienda ejecutar `clean test` para evitar conflictos si el editor compila clases parciales en `target/classes/`.

## Estructura del proyecto

```text
backend-ventas/
├── sql/
│   ├── schema.sql
│   └── data.sql
├── src/main/java/com/fuentes/ventas/
│   ├── config/       OpenApiConfig, SecurityConfig
│   ├── controller/   ProductoController, VentaController
│   ├── dto/          Producto*DTO, Venta*DTO, DetalleVenta*DTO, ErrorResponseDTO
│   ├── exception/    GlobalExceptionHandler, ResourceNotAvailable, StockInsuficienteException
│   ├── model/        Producto, Venta, DetalleVenta
│   ├── repository/   ProductoRepository, VentaRepository
│   └── service/      ProductoService, VentaService
└── src/test/java/com/fuentes/ventas/
    ├── ProductoControllerTest.java
    ├── VentaControllerTest.java
    └── VentasApplicationTests.java
```
