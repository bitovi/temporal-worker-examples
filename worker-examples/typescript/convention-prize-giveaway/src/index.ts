import { runWorker } from "./worker";
import { runServer } from "./server";

async function runServerAndWorker() {
  return Promise.all([runServer(), runWorker()]).catch((err) => {
    console.error(err);
    process.exit(1);
  });
}

runServerAndWorker();
