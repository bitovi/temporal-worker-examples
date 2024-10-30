import * as wf from "@temporalio/workflow";
import type * as activities from "./activities";
import type { Entry, startWorkflowInputObject } from "./types";

export const entrySignal = wf.defineSignal<[Entry]>("entrySignal");
export const entriesQuery = wf.defineQuery<Entry[]>("entriesQuery");

const {
  sendEmail,
  getWinnerBccEmails,
  getEntryBccEmails,
  setEntrySignupCounterValue,
  setWinnerSelectedCounterValue,
  getCurrentHour,
} = wf.proxyActivities<typeof activities>({
  startToCloseTimeout: "1 minute",
});

//Workflow that picks a random entrant to win every hour and notifies the winner
export async function giveawayWorkflow(
  inputs: startWorkflowInputObject,
): Promise<void> {
  const { startTime, endTime, sleepTime, maxWins } = inputs;
  const entryList: Entry[] = [];
  let winCounter = 0;

  await setWinnerSelectedCounterValue(0);
  await setEntrySignupCounterValue(0);

  //Get the entry from the signal and add it to the entryList if the email is not a duplicate
  wf.setHandler(entrySignal, async (entry: Entry) => {
    //Check if entry already exists
    const duplicateEntry = entryList.some((e) => e.email == entry.email);
    if (!duplicateEntry) {
      entryList.push({ ...entry, timeWon: null });

      await sendEmail({
        to: entry.email,
        bcc: await getEntryBccEmails(),
        name: entry.name,
        typeOfMessage: "entry",
      });

      await setEntrySignupCounterValue(entryList.length);
    }
  });

  wf.setHandler(entriesQuery, () => entryList);

  while (winCounter < maxWins) {
    await wf.sleep(sleepTime);

    const currentHour = await getCurrentHour();

    if (currentHour > startTime && currentHour < endTime) {
      const possibleWinners = entryList.filter(
        (entry) => entry.timeWon == null,
      );

      if (possibleWinners.length > 0) {
        const winner =
          possibleWinners[Math.floor(Math.random() * possibleWinners.length)];

        await sendEmail({
          to: winner.email,
          bcc: await getWinnerBccEmails(),
          name: winner.name,
          typeOfMessage: "winner",
        });

        winner.timeWon = new Date().toISOString();
        winCounter++;
        await setWinnerSelectedCounterValue(winCounter);
      }
    }
  }
}
