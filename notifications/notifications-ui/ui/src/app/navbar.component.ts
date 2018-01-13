import {Component} from '@angular/core';
import {AuthenticationService} from "./services/authentication.service";
import {BaseComponent} from "./base.component";

@Component({
  selector: 'navbar',
  styleUrls: ['./navbar.component.scss'],
  template: `
    <div class="ui fixed inverted menu">
      <div class="ui container">
        <a href="#" class="header item">
          <img class="logo" src="">
          Project Name
        </a>
        <a href="#" class="item">Home</a>
        <div class="right menu">
          <div class="item">
            <login *ngIf="!isLoggedIn()"></login>
            <div *ngIf="isLoggedIn()" >
                <button class="mini ui button" (click)="logout()">Logout</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class NavbarComponent extends BaseComponent {
  constructor(protected authService: AuthenticationService) {
    super(authService);
  }

  logout(): void {
    this.authService.logout();
  }
}
