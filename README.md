# Convertly

A file conversion system that converts documents and images across formats.

## Run locally

### Prerequisites

- Java 21
- Docker and Docker Compose

### 1. Configure environment variables

Copy the example environment file:

```bash
cp .env.example .env
```

The `.env` file provides the PostgreSQL credentials used by Docker Compose. Update the values if needed.

### 2. Start PostgreSQL

Start the PostgreSQL container:

```bash
docker compose up -d postgres
```

Wait until the database is healthy before proceeding.

Check the container status:

```bash
docker compose ps
```

### 3. Run the applications

Open two separate terminals from the project root.

**Terminal 1 — API**

```bash
./gradlew :api:bootRun
```

**Terminal 2 — Worker**

```bash
./gradlew :worker:bootRun
```

