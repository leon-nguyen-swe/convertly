# Convertly

A file conversion system that converts documents and images across formats.

## Run locally

### Prerequisites

- Java 21
- Docker and Docker Compose
- Git

### 1. Configure environment variables

Copy the example environment file:

```bash
cp .env.example .env
```

The `.env` file contains credentials for PostgreSQL, RabbitMQ, and RustFS. Update the values if needed.

Keep `.env` local and **never commit it**. The `.env.example` file should contain placeholder values, not real credentials.

### 2. Start the infrastructure

Start PostgreSQL, RabbitMQ, and RustFS:

```bash
docker compose up -d
```

Docker Compose starts the infrastructure and runs the `create-bucket` one-off service, which creates the `convertly` bucket in RustFS if it does not already exist.

Wait until the infrastructure services are healthy:

```bash
docker compose ps -a
```

Check that:

- `postgres` is running and healthy.
- `rabbitmq` is running and healthy.
- `storage` is running and healthy.
- `create-bucket` has exited with code `0`.

The `create-bucket` service is expected to exit after completing its task; it is not a long-running service.

To inspect logs if a service does not start correctly:

```bash
docker compose logs postgres
docker compose logs rabbitmq
docker compose logs storage
docker compose logs create-bucket
```

### 3. Access the local services

Once the containers are healthy, the following endpoints are available:

| Service | URL | Purpose |
|---|---|---|
| RabbitMQ Management | http://localhost:15672 | Manage queues and inspect messages |
| RustFS Console | http://localhost:9001 | Manage buckets and objects |
| RustFS S3 API | http://localhost:9000 | S3-compatible object storage API |
| PostgreSQL | `localhost:5432` | Application database |
| RabbitMQ AMQP | `localhost:5672` | Message broker endpoint |

Use the credentials configured in `.env` to access the RabbitMQ Management UI and RustFS Console.

The `convertly` bucket should already exist in the RustFS Console after the `create-bucket` service completes successfully.

### 4. Run the applications

Open two separate terminals from the project root.

**Terminal 1 — API**

```bash
./gradlew :api:bootRun
```

**Terminal 2 — Worker**

```bash
./gradlew :worker:bootRun
```

Ensure the required application environment variables are configured before starting the applications.

### 5. Stop the infrastructure

Stop and remove the containers while preserving named volumes:

```bash
docker compose down
```

To also remove the persisted PostgreSQL, RabbitMQ, and RustFS data:

```bash
docker compose down -v
```

**Warning:** `docker compose down -v` permanently deletes the data stored in these named volumes, including the local database and uploaded objects.