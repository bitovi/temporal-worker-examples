import metricsClient from "prom-client";

const Registry = metricsClient.Registry;
const metricsRegistry = new Registry();

export class MetricsCounter {
  name: string;
  help: string;
  _counter: metricsClient.Gauge<string>;

  constructor(name: string, help: string) {
    this.name = name;
    this.help = help;
    this._counter = new metricsClient.Gauge({
      name: this.name,
      help: this.help,
    });

    metricsRegistry.registerMetric(this._counter);
  }

  setValue(value: number): void {
    this._counter.set(value);
  }
}

export const entrySignupCounter = new MetricsCounter(
  "signups",
  "number of signups",
);
export const winnerSelectedCounter = new MetricsCounter(
  "wins",
  "number of wins",
);

export async function metrics(): Promise<string> {
  return metricsRegistry.metrics();
}
