import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {AuthHttp} from "angular2-jwt";
import {User} from "./types";

@Injectable()
export class UserService {

  constructor(private authHttp: AuthHttp) {
  }

  findById(id: number): Observable<User> {
    return this.authHttp.get(`/api/users/${id}`)
      .map(res => res.json());
  }

  me(): Observable<User> {
    return this.authHttp.get(`/api/users/me`)
      .map(res => res.json());
  }

}
