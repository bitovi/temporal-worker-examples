# Temporal Giveaway API


This app is for the Bitovi Booth at Temporal's Replay 2023 conference

## Running locally

Run the commands below to run the following locally:
- Temporal
- Postgres (for Temporal)
- The API server
- The Temporal Worker

First, you will need a `.env` file. It can be empty as all environment variables are passed in through the `docker-compose.dev.yml`. Run this command to create an empty file:

```bash
touch .env
```

Then use this command to run everything:

```bash
npm run dev:docker
```

To shut down everything:

```bash
npm run dev:docker:stop
```

## Running locally with Temporal Cloud

To run against Temporal cloud, you will need a `.env` file with the following values:

```
NAMESPACE="<Temporal namespace>"

TEMPORAL_HOST_URL="<Temporal namespace>.tmprl.cloud:7233"

TEMPORAL_CERT="-----BEGIN CERTIFICATE-----
<cert data>
-----END CERTIFICATE-----"

TEMPORAL_CERT_KEY="-----BEGIN RSA PRIVATE KEY-----
<private key data>
-----END RSA PRIVATE KEY-----"
JWT_SECRET=<JWT secret>
USER_1="<user info>"
```

Then run the following command to run the server and worker:

```bash
docker compose up --build
```

To shut them down:

```bash
docker compose down
```

## Restoring deployed app

1. Revert [PR #54](https://github.com/bitovi/temporal-giveaway-workflow/pull/54)
2. Update [Sendgrid Plan](https://app.sendgrid.com/account/products)
3. Enable publishing to [GitHub Pages](https://github.com/bitovi/temporal-giveaway-frontend/settings/pages) for the frontend
4. Add SendGrid IAM Rules to [Route 53](https://us-east-1.console.aws.amazon.com/route53/v2/hostedzones)
