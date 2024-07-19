# Temporal Golang Hello World Worker

This worker simply waits 30 seconds and then returns a string of Hello World.

## Usage

__Startup__

```bash
docker compose -f golang/hello-world/docker-compose.yaml up -d --build
```

__Execution__

```bash
temporal workflow start \
  --task-queue=hello-world-golang  \
  --type=Workflow \
  --address=localhost:7233 \
  --namespace=default
```