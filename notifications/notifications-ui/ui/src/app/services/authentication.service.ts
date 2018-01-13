import {Observable} from "rxjs/Observable";
import {Http} from "@angular/http";
import {AuthConfigConsts, tokenNotExpired} from "angular2-jwt";
import {Injectable} from "@angular/core";
import {Token} from "./types";

@Injectable()
export class AuthenticationService {

  constructor(private http: Http) {
    Observable.interval(5000)
      .map(() => this.isLoggedIn())
      .distinctUntilChanged()
      .subscribe(isLoggedIn => {
        console.log('is logged in', isLoggedIn);
      });
  }

  authenticate(username: string, password: string): Observable<Token> {
    return this.token(username, password);
  }

  logout(): void{
    localStorage.clear();
  }

  isLoggedIn(): boolean {
    return tokenNotExpired();
  }

  getToken(): string {
    return localStorage.getItem(AuthConfigConsts.DEFAULT_TOKEN_NAME);
  }

  private token(username: string, password: string): Observable<Token> {
    return this.http.post('/auth/token', {
      username: username,
      password: password
    }).map(res => res.json() as Token)
      .do(token => localStorage.setItem(AuthConfigConsts.DEFAULT_TOKEN_NAME, token.token));
  }
}
