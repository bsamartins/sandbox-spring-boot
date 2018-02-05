import {Component} from '@angular/core';
import {AuthenticationService} from "./services/authentication.service";
import {BaseComponent} from "./base.component";

@Component({
  selector: 'home-view',
  template: `
    <h1>Welcome</h1>
  `
})
export class HomeViewComponent extends BaseComponent {

  constructor(protected authenticationService: AuthenticationService) {
    super(authenticationService);
  }
}
