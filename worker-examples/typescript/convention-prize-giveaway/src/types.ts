//Interface for the entry object
export interface Entry {
  name: string;
  email: string;
  company: string;
  title: string;
  timeWon: string | null;
}
// Schema for the POST request
export const schema = {
  body: {
    type: "object",
    required: ["name", "email"],
    properties: {
      name: { type: "string" },
      email: { type: "string" },
      company: { type: "string" },
      title: { type: "string" },
      contactByEmail: { type: "boolean" },
    },
  },
};

export interface startWorkflowInputObject {
  sleepTime: number;
  maxWins: number;
  startTime: number;
  endTime: number;
}

export interface User {
  username: string;
  password: string;
}
