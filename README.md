# CRUD de Productos - Spring Boot + PostgreSQL

Proyecto Maven para IntelliJ del modulo Gestion de Producto adaptado a los cambios mas recientes del equipo.

## Que incluye ahora

- CRUD de productos
- Codigo generado automaticamente en backend
- Formulario de producto simplificado con nombre tipo y tarifa de produccion
- Columna de tarifa de produccion en el listado
- Boton global para configurar tarifa de produccion
- Accion individual para cambiar tarifa por producto
- Historial de tarifas por medio de la entidad `TarifaProduccion`
- Entidad `UnidadMedida`
- Entidad `ProductoUnidad`
- Catalogo basico de unidades de medida
- Restriccion de eliminacion cuando el producto tiene movimientos historicos
- PostgreSQL como motor de base de datos

## Tecnologias

- Java 21 o superior
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- PostgreSQL

## Como correrlo

1. Crea la base de datos `gestion_productos` en PostgreSQL.
2. Revisa `src/main/resources/application.properties`.
3. Abre el proyecto en IntelliJ como proyecto Maven.
4. Espera que descargue dependencias.
5. Ejecuta `GestionProductosApplication`.
6. Entra a `http://localhost:8080/productos`.

## Rutas principales

- `/productos`
- `/productos/nuevo`
- `/tarifas-produccion`
- `/unidades-medida`

## Notas

- El login redirige al listado de productos para fines academicos.
- El codigo no se muestra en alta pero si en el listado.
- Hibernate actualiza las tablas con `spring.jpa.hibernate.ddl-auto=update`.
