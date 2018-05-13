import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {NgForm} from "@angular/forms";
import {AuthenticationService} from "../services/authentication.service";
import {BaseComponent} from "../base.component";
import {MessageService} from "../services/message.service";
import {Subscription} from "rxjs/Subscription";
import {Observable} from "rxjs/Observable";
import {UserService} from "../services/user.service";
import {Message, User} from "../services/types";

@Component({
  selector: 'chat-messages',
  styleUrls: ['./chat-messages.component.scss'],
  template: `    
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
  `
})
export class ChatMessagesComponent extends BaseComponent implements OnInit, OnDestroy, OnChanges {
  private messagesSubscription: Subscription;
  private userCache: Map<string, User> = new Map();

  @Input() users: Observable<User[]>;

  messages: MessageEntry[] = [];
  posting: boolean;


  constructor(protected authenticationService: AuthenticationService,
              private messageService: MessageService,
              private userService: UserService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.users.do(users => {
      this.addUsersToCache(users);
    }).flatMap(() => this.loadMessages()).subscribe(res => {
      res.forEach(m => this.messages.unshift(m));
    });
    // this.start();
  }

  ngOnDestroy(): void {
    this.stop();
  }

  private loadMessages(): Observable<MessageEntry[]> {
    return this.messageService.getMessages().flatMap(messages => {
      let userIds: Set<string> = messages.map(m => m.userId)
        .reduce((acc, userId) => {
          if(this.userCache.has(userId)) {
            return acc;
          } else {
            return acc.add(userId);
          }
        }, new Set<string>());
      let userObservables: Observable<User>[] = Array.from(userIds)
        .map(id => this.userService.findById(id));

      console.log(this.userCache);
      console.log(userObservables);

      return Observable.forkJoin(
        Observable.of(messages),
        userObservables.length > 0 ? Observable.forkJoin(userObservables) : Observable.of([])
      ).map(res => {
        let messages: Message[] = res[0];
        let users: User[] = res[1];
        this.addUsersToCache(users);
        return messages.map(msg => {
          return {
            user: this.userCache.get(msg.userId),
            message: msg
          }
        });
      });
    });
  }

  addUsersToCache(users: User[]): void {
    this.userCache = users.reduce((acc, u) => acc.set(u.id, u), this.userCache);
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

  private findUser(id: string): Observable<User> {
    let user: User = this.userCache.get(id);
    if(user) {
      return Observable.of(user);
    } else {
      return this.userService.findById(id)
        .do(user => this.userCache.set(id, user));
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['chatId'] && !changes['chatId'].isFirstChange()) {
      this.loadMessages().subscribe(res => {
        res.forEach(m => this.messages.unshift(m));
      });
    }
  }
}

export interface MessageEntry {
  user: User;
  message: Message;
}
