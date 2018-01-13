import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {Message} from "./types";
import {AuthSse} from "./authsse";
import {AuthHttp} from "angular2-jwt";

@Injectable()
export class MessageService {

  constructor(private authSse: AuthSse, private authHttp: AuthHttp) {
  }

  send(message: string) {
    return this.authHttp.post('/api/messages', message);
  }

  stream(): Observable<Message> {
    return this.authSse.create('/api/messages/stream', null)
      .map(m => JSON.parse(m.data) as Message);
  }

  getMessages(): Observable<Message[]> {
    return this.authHttp.get('/api/messages')
      .map(res => res.json());
  }

}
