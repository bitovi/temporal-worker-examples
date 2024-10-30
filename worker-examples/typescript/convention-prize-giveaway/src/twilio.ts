import { readFileSync } from "node:fs";
import mail from "@sendgrid/mail";
import type { MailData } from "@sendgrid/helpers/classes/mail";
import type { AttachmentData } from "@sendgrid/helpers/classes/attachment";

import "dotenv/config";

mail.setApiKey(process.env.SENDGRID_API_KEY || "");

const emailTemplates: { [key: string]: string } = {
  entry: readFileSync(require.resolve("./email-templates/entry.html"), {
    encoding: "utf-8",
  }),
  winner: readFileSync(require.resolve("./email-templates/winner.html"), {
    encoding: "utf-8",
  }),
  "winner-notification": readFileSync(
    require.resolve("./email-templates/winner-notification.html"),
    { encoding: "utf-8" },
  ),
};

const bitoviKrakenContent = readFileSync(
  require.resolve("./email-templates/assets/bitovi-kraken.png"),
).toString("base64");
const temporalLogoContent = readFileSync(
  require.resolve("./email-templates/assets/temporal-logo.png"),
).toString("base64");
const minionPartyHornContent = readFileSync(
  require.resolve("./email-templates/assets/minion-party-horn.gif"),
).toString("base64");

const emailAttachments: { [key: string]: AttachmentData[] } = {
  entry: [
    {
      content: bitoviKrakenContent,
      contentId: "bitovi-kraken.png",
      filename: "bitovi-kraken.png",
      type: "image/png",
      disposition: "inline",
    },
    {
      content: temporalLogoContent,
      contentId: "temporal-logo-entry.png",
      filename: "temporal-logo.png",
      type: "image/png",
      disposition: "inline",
    },
  ],
  winner: [
    {
      content: minionPartyHornContent,
      contentId: "minion-party-horn.gif",
      filename: "minion-party-horn.gif",
      type: "image/gif",
      disposition: "inline",
    },
    {
      content: temporalLogoContent,
      filename: "temporal-logo.png",
      contentId: "temporal-logo-winner.png",
      type: "image/png",
      disposition: "inline",
    },
  ],
};

const generateEmailDetails = (
  name: string,
  typeOfMessage: string,
): MailData => {
  const capitalizedName = name.charAt(0).toUpperCase() + name.slice(1);

  const emailDetails: MailData = {
    from: String(process.env.FROM_EMAIL),
    subject: "",
    html: "",
    attachments: [] as AttachmentData[],
  };

  if (!(typeOfMessage in emailTemplates)) {
    throw new Error(`Unknown email type - ${typeOfMessage}`);
  }

  switch (typeOfMessage) {
    case "entry":
      emailDetails.subject = "You Have Entered Bitovi's Replay Giveaway";
      break;

    case "winner":
      emailDetails.subject = "You Have Won Bitovi's Replay Giveaway";
      break;

    case "winner-notification":
      emailDetails.subject =
        "A Winner Has Been Selected for Bitovi's Replay Giveaway";
      break;

    default:
  }

  const emailTemplate = emailTemplates[typeOfMessage];
  emailDetails.html = emailTemplate.replace("{{ name }}", capitalizedName);

  emailDetails.attachments = emailAttachments[typeOfMessage];

  return emailDetails;
};

interface SendEmailInput {
  to: string;
  bcc: string[];
  name: string;
  typeOfMessage: string;
}
export const sendEmail = async (
  input: SendEmailInput,
): Promise<SendEmailInput> => {
  const { to, bcc } = input;

  const { from, subject, html, attachments } = generateEmailDetails(
    input.name,
    input.typeOfMessage,
  );

  const msg = {
    to,
    bcc,
    from,
    subject,
    html: html!,
    attachments,
  };

  await mail.send({
    ...msg,
    attachments: (msg.attachments || []).map((attachment: any) => ({
      ...attachment,
      content_id: attachment.contentId,
    })),
  });

  return input;
};
