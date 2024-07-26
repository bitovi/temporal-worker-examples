// workflows.ts
import { proxyActivities } from "@temporalio/workflow";
import type * as activities from "./activities";

// Proxying the activities to be used within the workflow
const { greet } = proxyActivities<typeof activities>({
  startToCloseTimeout: "1 minute",
});

export async function exampleWorkflow(name: string): Promise<string> {
  return await greet(name);
}
