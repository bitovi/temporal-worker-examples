# Temporal Golang Hello World Worker

This worker simply waits 30 seconds and then returns a string of Hello World. The activity concurrency is set to 1, and the workflow concurrency is set to 2 (this is the minimum value for the [Golang SDK](https://github.com/temporalio/sdk-go/blob/9c422211a412a6a194ecda548f21790ddc66d988/internal/internal_worker.go#L1520-L1526)).

This example is designed to emulate a resource intensive workflow where only a small number of tasks can run on a given worker, and those tasks take a relatively long time to complete.

## Usage

__Startup__

```bash
docker compose up -d --build
```

__Execution__

```bash
temporal workflow start \
  --task-queue=hello-world  \
  --type=Workflow \
  --address=localhost:7233 \
  --namespace=default
```