# Temporal Eager Workflow Start Demo

The purpose of the repo is to demonstrate the [`EnableEagerStart`](https://temporal.io/blog/improving-latency-with-eager-workflow-start) and [Update](https://docs.temporal.io/develop/go/message-passing#updates) features of [Temporal](https://temporal.io/) simultaneously.

The example is a simplified personal payments system, Think Venmo. This example has two parts, a user can (1) request payment from another user, and (2) the recipient can accept or decline.

The Update feature is used for the latter step, and the workflow is started with `EnableEagerStart` to improve startup time for the initial request.

## Demo

__Create a payment request__

Eagerly start a workflow to request money from another user. It will immediately create a payment request entity, notify the target user, and wait for an update from a user response.

```bash
curl --location 'http://0.0.0.0:8080/payment-request' \
--header 'Content-Type: application/json' \
--data '{
  "userId": "usr_0dc9d91f-f102-47ca-9e47-bcd75babfe20",
  "amount": 20.99
}'
```
```json
{
  "paymentRequestId": "req_8eb7d5a2-4ac4-4b93-b8ce-ce7809ab8a04"
}
```

__Accept a payment request__

Accept a payment by using an update call on the running workflow and passing in the accepted status from the user response.

```bash
curl --location 'http://localhost:8080/payment-response' \
--header 'Content-Type: application/json' \
--data '{
  "accepted": true,
  "paymentRequestId": "req_8eb7d5a2-4ac4-4b93-b8ce-ce7809ab8a04"
}'
```

```json
{
  "paymentId": "pay_3903ed76-cfd2-4d9b-a4bc-2fc2f6d8254b"
}
```