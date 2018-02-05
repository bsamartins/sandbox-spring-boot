import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {AuthenticationService} from "../services/authentication.service";
import {BaseComponent} from "../base.component";
import {MessageService} from "../services/message.service";
import {Subscription} from "rxjs/Subscription";
import {Observable} from "rxjs/Observable";
import {UserService} from "../services/user.service";
import {Message, User} from "../services/types";
import {animate, keyframes, query, stagger, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'messages-view',
  animations: [
    trigger('flyInOut', [
      transition('* => *', [
        query(':enter', style({ opacity: 0 }), {optional: true}),
        query(':enter', stagger('100ms', [
          animate('500ms ease-in',
            keyframes([
              style({opacity: 0, transform: 'translateY(-75%)', offset: 0}),
              style({opacity: 1, transform: 'translateY(0)', offset: 1.0})
            ])
          )]), {optional: true})
      ])
    ])
  ],
  template: `
    <div class="ui segment">
      <div *ngIf="posting" class="ui inverted active dimmer">
        <div class="ui loader"></div>
      </div>
      <form class="ui form" #messageForm="ngForm">
        <div class="field">
          <label>Message</label>
          <input type="text" name="message" placeholder="Message" required="true" ngModel>
        </div>
        <button class="ui button" (click)="messageForm.reset()">Reset</button>
        <button class="ui button" (click)="submit(messageForm)" [disabled]="messageForm.invalid">Send</button>
      </form>
    </div>

    <div class="ui feed" [@flyInOut]="messages.length">
      <ng-container *ngFor="let entry of messages; trackBy: trackByFn">
        <div class="event">
          <div class="content">
            <div class="summary">
              <div class="user">{{ entry.user.username }}</div>
              <div class="date">{{ entry.message.timestamp }}</div>
            </div>
            <div class="extra text">
                {{ entry.message.text }}
            </div>
          </div>
        </div>
      </ng-container>
    </div>
  `
})
export class MessagesViewComponent extends BaseComponent implements OnInit, OnDestroy {
  private messagesSubscription: Subscription;
  private userCache: Map<number, User> = new Map();
  messages: MessageEntry[] = [];
  posting: boolean;


  constructor(protected authenticationService: AuthenticationService,
              private messageService: MessageService,
              private userService: UserService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.messageService.getMessages().flatMap(messages => {
      let userIds: Set<number> = messages.map(m => m.userId)
        .reduce((acc, userId) => acc.add(userId), new Set<number>());
      let userObservables: Observable<User>[] = Array.from(userIds)
        .map(id => this.userService.findById(id));

      return Observable.forkJoin(
        Observable.of(messages),
        userObservables.length > 0 ? Observable.forkJoin(userObservables) : Observable.of([])
      );
    }).subscribe(res => {
      let messages: Message[] = res[0];
      let users: User[] = res[1];
      this.userCache = users.reduce((acc, u) => acc.set(u.id, u), this.userCache);
      messages.filter(m => this.userCache.get(m.userId))
        .map(msg => {
          return {
            user: this.userCache.get(msg.userId),
            message: msg
          }
        }).forEach(m => {
          this.messages.unshift(m);
      });

    });
    // this.start();
  }

  ngOnDestroy(): void {
    this.stop();
  }

  submit(form: NgForm) {
    this.posting = true;
    this.messageService.send(form.value.message).subscribe(() => {
      form.reset();
    }, err => {
      console.error(err);
    }, () => {
      this.posting = false;
    });
  }

  start(): void {
    this.messagesSubscription = this.messageService.stream()
      .flatMap(msg => this.findUser(msg.userId).map(user => {
        return {
          user: user,
          message: msg
        } as MessageEntry;
      }))
      .subscribe(m => {
        console.log('got message', m.message);
        this.messages.unshift(m);
      }, err => {
        console.log(err);
      }, () => console.log('complete'));
  }

  stop(): void {
    if(this.messagesSubscription) {
      this.messagesSubscription.unsubscribe();
    }
  }

  trackByFn(index: number, entry: MessageEntry): any {
    return entry.message.id;
  }

  private findUser(id: number): Observable<User> {
    let user: User = this.userCache.get(id);
    if(user) {
      return Observable.of(user);
    } else {
      return this.userService.findById(id)
        .do(user => this.userCache.set(id, user));
    }
  }
}

export interface MessageEntry {
  user: User;
  message: Message;
}
