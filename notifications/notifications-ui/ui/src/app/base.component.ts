import {AuthenticationService} from "./services/authentication.service";

export class BaseComponent {
  constructor(protected authService: AuthenticationService) {
  }

  protected isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
