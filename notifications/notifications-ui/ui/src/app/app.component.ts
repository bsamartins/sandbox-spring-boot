import {Component} from '@angular/core';
import {BaseComponent} from "./base.component";
import {AuthenticationService} from "./services/authentication.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent extends BaseComponent {
  constructor(protected authService: AuthenticationService) {
    super(authService);
  }
}
