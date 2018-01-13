import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {Sse} from "./sse";
import {AuthenticationService} from "./authentication.service";

@Injectable()
export class AuthSse {

  constructor(private sse: Sse, private authService: AuthenticationService) {

  }

  create(url: string, options?: any): Observable<any> {
    let token: string = this.authService.getToken();
    let opts: any = options || {};
    opts.headers = opts.headers || {};
    opts.headers['Authorization'] = `Bearer ${token}`;
    return this.sse.create(url, opts);
  }

}
