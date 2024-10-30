import { NativeConnection, Worker, Runtime } from "@temporalio/worker";
import * as activities from "./activities";
import "dotenv/config";
import { getTemporalClientOptions } from "./temporal-client";

export async function runWorker(): Promise<void> {
  Runtime.install({
    telemetryOptions: {
      metrics: { prometheus: { bindAddress: "0.0.0.0:8077" } },
    },
  });

  const temporalClientOptions = getTemporalClientOptions();
  const connection = await NativeConnection.connect(temporalClientOptions);

  const worker = await Worker.create({
    workflowsPath: require.resolve("./workflows"),
    activities,
    taskQueue: "temporal-giveaway",
    connection,
    namespace: process.env.NAMESPACE,
  });

  await worker.run();
}
