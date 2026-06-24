# e-reader-backend

## Project overview

Spring Boot backend project setup for the e-reader and webtoon admin system. This initial setup includes PostgreSQL, Redis cache management, Flyway, JPA, Validation, and Spring Security dependencies without authentication APIs or endpoint implementations.

## RS256 JWT keys

Generate local RSA keys before running the backend:

```bash
mkdir -p keys
openssl genrsa -out keys/private.pem 2048
openssl rsa -in keys/private.pem -pubout -out keys/public.pem
```

`private.pem` is used to sign JWTs. `public.pem` is used to verify JWTs.

`private.pem` must never be committed to GitHub. The `keys` folder is mounted into Docker at `/app/keys`.

## How to run with Docker

Create a local environment file and set a private database password:

```bash
cp .env.example .env
```

Edit `DB_PASSWORD` in `.env`, then run:

```bash
docker compose up -d --build
```

## How to stop Docker

```bash
docker compose down
docker compose down -v
```

## How to check logs

```bash
docker compose logs -f backend
docker compose logs -f postgres
docker compose logs -f redis
```

## How to connect local Mac pgAdmin to PostgreSQL

Host: localhost

Port: 5432

Database: alpha_web_default

Username: alphaadmin

Password: the `DB_PASSWORD` value from your local `.env`

## How to connect to Redis locally

Host: localhost

Port: 6379

## Maven commands

```bash
mvn clean package
mvn spring-boot:run
```
