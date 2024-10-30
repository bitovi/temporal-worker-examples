import "dotenv/config";
import type { User } from "./types";
// @ts-ignore
import jwt = require("jsonwebtoken");

// eslint-disable-next-line @typescript-eslint/no-var-requires
const bcrypt = require("bcryptjs");

export const getUserList = (): User[] => {
  const userList: User[] = [];
  for (const field in process.env) {
    if (field.includes("USER_")) {
      const user = process.env[field];
      const username = user?.split("::")[0] || "";
      const password = user?.split("::")[1] || "";
      userList.push({ username, password });
    }
  }
  return userList;
};

export const checkUser = (
  userList: User[],
  username: string,
  password: string,
): boolean => {
  const user = userList.find((u) => u.username === username);
  if (user) {
    const match = bcrypt.compareSync(password, user.password);
    return !!match;
  }
  return false;
};

export const userUnauthorized = (userList: User[], token: string): boolean => {
  const decodedToken = jwt.verify(token, process.env.JWT_SECRET);
  return !userList.find((u) => u.username === decodedToken.username);
};
