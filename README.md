# Bitovi Temporal Examples

This repo holds examples of Temporal workflows in Goland / Typescript.

## Table of Contents
1. [Setup](#setup)
1. [Temporal Architecture](#temporal-architecture)
1. [Examples: Golang](#golang)
1. [Examples: Typescript](#typescript)

## Setup

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
    - All worker-example folders contain a compose.yaml that atr used to compile worker code & build a worker.
    - Reusable containers within Docker (databases/temporal-dev-server) are defined within `/temporal-architecture/common.yaml`
    - Reusable containers targeting external services (Temporal Cloud) are defined within `/temporal-architecture/common-cloud.yaml`
        - [Bitovi's Temporal Cloud Certificate](https://bitovi.1password.com/vaults/z75qtkoicixtf5wbyu6ctr7ngq/allitems/y3nijldomzjm34joflb34tedkm/)
            - Copy the cert into `./certs/bitovi.crt`
            - Copy the key into `./certs/bitovi.key`

<a name="Metrics"></a><a name="2.4"></a>
- [2.4](#temporal-metrics) Temporal Metrics:

    - prometheus
    - grafana

## Code examples

Run the following command to run the server and worker:

```bash
docker compose up --build
```

To shut them down:

```bash
docker compose down
```

### Golang

- [__Distributed Systems__](./worker-examples/golang/distributed-systems/README)
- [__Eager start and update__](./worker-examples/golang/eager-start-and-update/README)
- [__Hello World__](./worker-examples/golang/hello-world/README)


### Typescript

- [__AI Pipeline__](./worker-examples/typescript/ai-pipeline/README)
- [__BPMN tool__](./worker-examples/typescript/bpmn-tool/README)
- [__Convention Prize Giveaway__](./worker-examples/typescript/convention-prize-giveaway/README)
- [__Restaurant Menu Publish__](./worker-examples/typescript/restaurant-menu-publish/README)
