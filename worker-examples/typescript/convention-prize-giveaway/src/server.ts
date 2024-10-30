import Fastify from "fastify";
import cors from "@fastify/cors";
import type { WorkflowHandle } from "@temporalio/client";
// @ts-ignore
import jwt = require("jsonwebtoken");
import { entrySignal, entriesQuery } from "./workflows";
import type { User } from "./types";
import { schema } from "./types";
import { startTemporalClient } from "./temporal-client";
import { checkUser, getUserList, userUnauthorized } from "./helperFunctions";
import { metrics } from "./metrics";

const port = process.env.PORT || 3000;

const fastify = Fastify({
  logger: true,
});

let handle: WorkflowHandle;

fastify.register(cors, () => {
  return (
    req: { headers: { origin: string } },
    callback: (
      arg0: null,
      arg1: {
        origin: boolean;
      },
    ) => void,
  ) => {
    const corsOptions = {
      origin: true,
    };
    if (/^localhost$/m.test(req.headers.origin)) {
      corsOptions.origin = false;
    }
    callback(null, corsOptions);
  };
});

fastify.get("/healthcheck", async function handler(request, reply) {
  reply.code(200).send({ status: "up" });
  return;
});

fastify.get("/metrics", async function handler(request, reply) {
  return metrics();
});

fastify.get("/", async function handler(request, reply) {
  if (
    request.headers.authorization &&
    userUnauthorized(usersList, request.headers.authorization)
  ) {
    return reply.code(401).send({ message: "Unauthorized" });
  }
  const queryResult = await handle.query(entriesQuery);
  if (queryResult.length > 0) {
    return reply.code(200).send(queryResult);
  }
  return reply.code(404).send({ message: "No winners yet" });
});

fastify.post("/", { schema }, async function handler(request, reply) {
  if (
    request.headers.authorization &&
    userUnauthorized(usersList, request.headers.authorization)
  ) {
    return reply.code(401).send({ message: "Unauthorized" });
  }
  await handle.signal(entrySignal, request.body);
  return reply.code(200).send({ message: "Entry added" });
});

fastify.post(
  "/login",
  async function handler(request: Record<string, any>, reply) {
    const body = request.body;
    const username = body["username"];
    const password = body["password"];
    if (checkUser(usersList, username, password)) {
      //generate token
      const token = jwt.sign({ username }, process.env.JWT_SECRET);
      return reply
        .code(200)
        .send({ message: "Login successful", token: token });
    } else {
      return reply.code(401).send({ message: "Invalid username or password" });
    }
  },
);

let usersList: User[];

export async function runServer(): Promise<void> {
  try {
    usersList = getUserList();
    handle = await startTemporalClient();
    await fastify.listen(port, "0.0.0.0");
    // eslint-disable-next-line
    console.log(`server listening on ${port}`);
  } catch (err) {
    fastify.log.error(err);
    process.exit(1);
  }
}
