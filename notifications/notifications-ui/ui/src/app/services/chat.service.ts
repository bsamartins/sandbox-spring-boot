import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {Chat, ChatCreate} from "./types";
import {AuthHttp} from "angular2-jwt";

@Injectable()
export class ChatService {

  constructor(private authHttp: AuthHttp) {
  }

  create(group: ChatCreate): Observable<Chat> {
    return this.authHttp.post('/api/chats', group).map(r => r.json());
  }

  find(): Observable<Chat[]> {
    return this.authHttp.get('/api/chats').map(r => r.json());
  }

  search(q: string): Observable<Chat[]> {
    return this.authHttp.get('/api/chats', {
      search: {
        query: q
      }
    }).map(r => r.json());
  }

  join(chat: Chat): Observable<any> {
    return this.authHttp.post(`/api/chats/${chat.id}/users`, null);
  }

}
