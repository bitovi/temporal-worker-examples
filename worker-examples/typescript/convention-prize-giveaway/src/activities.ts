import { entrySignupCounter, winnerSelectedCounter } from "./metrics";

export * from "./twilio";

export async function setEntrySignupCounterValue(
  value: number,
): Promise<number> {
  entrySignupCounter.setValue(value);
  return value;
}

export async function setWinnerSelectedCounterValue(
  value: number,
): Promise<number> {
  winnerSelectedCounter.setValue(value);
  return value;
}

export const getWinnerBccEmails = async (): Promise<string[]> => {
  const emailList = String(process.env.WINNER_BCC_EMAILS);
  return emailList.split(",");
};

export const getEntryBccEmails = async (): Promise<string[]> => {
  const emailList = String(process.env.ENTRY_BCC_EMAILS);
  return emailList.split(",");
};

export const getCurrentHour = async (): Promise<number> => {
  return new Date().getHours();
};
