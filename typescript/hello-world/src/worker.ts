// worker.ts
import { Worker } from "@temporalio/worker";
import * as activities from "./activities";

async function run() {
  // Create a worker and register the workflows and activities
  const worker = await Worker.create({
    workflowsPath: require.resolve("./workflows"),
    activities,
    taskQueue: "example-task-queue",
  });

  // Start accepting tasks on the task queue
  await worker.run();
}

run().catch((err) => {
  console.error("Worker failed:", err);
  process.exit(1);
});
