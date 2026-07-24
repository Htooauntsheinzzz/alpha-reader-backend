# e-reader-backend

## Project overview

Spring Boot backend for the e-reader and webtoon admin system. It includes PostgreSQL, Redis caching, RabbitMQ messaging, Flyway, JPA, validation, and RS256 JWT authentication.

## RS256 JWT keys

Generate local RSA keys before running the backend:

```bash
mkdir -p keys
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out keys/private.pem
openssl rsa -in keys/private.pem -pubout -out keys/public.pem
```

`private.pem` is used to sign JWTs. `public.pem` is used to verify JWTs.

`private.pem` must never be committed to GitHub. The `keys` folder is mounted into Docker at `/app/keys`.

## How to run with Docker

Create a local environment file and set a private database password:

```bash
cp .env.example .env
```

Edit `DB_PASSWORD` and `RABBITMQ_PASSWORD` in `.env`, then run:

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
docker compose logs -f rabbitmq
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

## How to connect to RabbitMQ locally

AMQP host: localhost

AMQP port: 5672

Management UI: http://localhost:15672

Username and password: the `RABBITMQ_USERNAME` and `RABBITMQ_PASSWORD` values from `.env`

The application publishes transactional outbox events to the durable `alpha.events` topic exchange. The `alpha.audit` queue receives all routing keys, and messages rejected by consumers are routed to `alpha.audit.dlq`.

## Maven commands

```bash
mvn clean package
mvn spring-boot:run
```
