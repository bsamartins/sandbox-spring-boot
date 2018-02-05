import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {Group, GroupCreate, Message} from "./types";
import {AuthSse} from "./authsse";
import {AuthHttp} from "angular2-jwt";

@Injectable()
export class GroupService {

  constructor(private authHttp: AuthHttp) {
  }

  create(group: GroupCreate): Observable<Group> {
    return this.authHttp.post('/api/groups', group).map(r => r.json());
  }

  find(): Observable<Group[]> {
    return this.authHttp.get('/api/groups').map(r => r.json());
  }

}
