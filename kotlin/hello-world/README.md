# Temporal Kotlin Hello World Worker

This worker simply returns a string of Hello World.

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
  --namespace=default
```