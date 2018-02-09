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

export class Chat {
  id: string;
  name: string;
  pictureId: string;
  pictureUri: string;
}

export class ChatCreate {
  name: string;
  picture: File;
}

export class File {
  name: string;
  content: string;
  mediaType: string;
}
