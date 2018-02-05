import {Component} from '@angular/core';
import {BaseComponent} from "./base.component";
import {AuthenticationService} from "./services/authentication.service";

@Component({
  template: `
    <div class="ui main text container" >
      <router-outlet></router-outlet>
    </div>
  `,
  selector: 'chat-view',
  styleUrls: ['./main-view.component.scss']
})
export class MainViewComponent extends BaseComponent {

  constructor(protected authenticationService: AuthenticationService) {
    super(authenticationService);
  }

}
