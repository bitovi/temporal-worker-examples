# Temporal Kotlin Data Fetching Worker

This worker fetches some random bytes of the specified length over HTTP, converts them to hex, and returns it as a string.

## Usage

__Startup__

```bash
docker compose -f kotlin/hello-world/docker-compose.yaml up -d --build
```

__Execution__

```bash
temporal workflow start \
  --task-queue=hello-world-kotlin  \
  --type=Workflow \
  --address=localhost:7233 \
  --namespace=default \
  --input=32
```