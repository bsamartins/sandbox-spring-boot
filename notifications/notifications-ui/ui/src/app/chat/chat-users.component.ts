import {Component, Input} from '@angular/core';
import {User} from "../services/types";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'chat-users',
  template: `
    <div class="ui middle aligned list">
      <div class="ui inverted dimmer active" *ngIf="loading">
        <div class="ui text loader">Loading</div>
      </div>
      <div *ngFor="let user of users | async" class="item">
        <img class="ui avatar image" src="{{ user.pictureUri }}">
        <div class="content">
          <div class="header">{{ user.username }}</div>
        </div>
      </div>
    </div>
  `
})
export class ChatUsersComponent {

  @Input() users: Observable<User>;
  @Input() loading: boolean;

}
