export interface Message {
  id: number;
  text: string;
  timestamp: string;
  userId: number;
}

export interface Token {
  type: string;
  token: string;
}

export interface User {
  id: number;
  username: string;
}
