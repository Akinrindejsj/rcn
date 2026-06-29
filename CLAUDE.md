# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

- `./mvnw spring-boot:run` — Start the app (runs on port 8060)
- `./mvnw compile` — Compile without running
- `./mvnw test` — Run tests
- `./mvnw test -Dtest=RcnApplicationTests` — Run a single test class
- `./mvnw clean package` — Build the JAR (skipping tests: `-DskipTests`)

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Tech Stack

- **Spring Boot 4.1.0** with Java 17
- **Spring Data JDBC** (not JPA/Hibernate — uses `spring-boot-starter-data-jdbc`)
- **PostgreSQL** via JDBC (`JDBC_DATABASE_URL` env var, defaults to `localhost:5432/rcn`)
- **Spring Integration** (AMQP, HTTP, JDBC, WS channels)
- **RabbitMQ** via Spring AMQP + Spring Integration AMQP
- **Thymeleaf** with `thymeleaf-layout-dialect` for server-rendered HTML
- **Tailwind CSS** via CDN (`cdn.tailwindcss.com`) — no build step, config is inline in `layout.html`
- **Lombok** for boilerplate reduction

## Architecture

This is a server-rendered web application. There is no JavaScript framework on the client side — only inline Tailwind and vanilla JS for minor interactivity (hamburger menu toggle).

### Template System

All pages use Thymeleaf's layout dialect:
- **`layout.html`** — shared shell with nav bar, footer, global CSS (Tailwind config + custom animations), and Google Fonts (Bebas Neue, Inter, Playfair Display, JetBrains Mono)
- **`pages/*.html`** — each decorates layout via `layout:decorate="~{layout}"` and provides a `layout:fragment="content"` block

### Routing

`PageController` serves all current routes — each method returns a Thymeleaf view name (no `@ResponseBody`, no REST API yet). Routes map 1:1 to template files under `templates/pages/`.

### Design Tokens

The Tailwind config in `layout.html` defines a custom color palette (`rev-red`, `rev-black`, `rev-ink`, `rev-gold`, `rev-page`, etc.) and font families (`display`, `body`, `serif`, `mono`). Use these tokens when editing templates — do not introduce raw hex colors or ad-hoc font stacks.

### Content Pattern

Pages currently contain **hardcoded content** (articles, quotes, stats). Image/video placeholders use the `.img-placeholder` and `.vid-placeholder` CSS classes defined in `layout.html`. These are intentional stand-ins — real media assets replace them when available.

## Configuration

- `application.properties` — sets `server.port=8060`
- `application.yml` — datasource config, JPA/Hibernate settings, multipart limits (20MB)
- Datasource defaults to local PostgreSQL; override with `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD` env vars
- Hibernate DDL auto is `update` — schema evolves on startup
