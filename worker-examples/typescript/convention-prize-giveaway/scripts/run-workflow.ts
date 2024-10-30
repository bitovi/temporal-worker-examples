import { Connection, Client } from "@temporalio/client";
import { giveawayWorkflow } from "../src/workflows";

import "dotenv/config";
import { getTemporalClientOptions } from "../src/temporal-client";
import type { startWorkflowInputObject } from "../src/types";

export async function runClient(): Promise<void> {
  const input: startWorkflowInputObject = JSON.parse(process.argv.slice(2)[0]);

  const connection = await Connection.connect(getTemporalClientOptions());
  const client = new Client({
    connection,
    namespace: process.env.NAMESPACE,
  });

  const handle = await client.workflow.start(giveawayWorkflow, {
    args: [input],
    taskQueue: "temporal-giveaway",
    workflowId: "giveaway-workflow",
    workflowTaskTimeout: "1 minute",
  });

  await handle.result();
}

runClient().catch((err) => {
  console.error(err);
  process.exit(1);
});
