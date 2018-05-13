import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../base.component";
import {AuthenticationService} from "../services/authentication.service";
import {ActivatedRoute} from "@angular/router";
import {User} from "../services/types";
import {Observable} from "rxjs/Observable";
import {UserService} from "../services/user.service";
import {ChatService} from "../services/chat.service";

@Component({
  selector: 'chat',
  styleUrls: ['./chat.component.scss'],
  template: `
    <div #chatGrid class="ui grid">
      <div class="row">
        <div class="four wide column">
          <chat-list [activeChatId]="chatId"></chat-list>
        </div>
        <div class="messages eight wide column">
          <chat-messages *ngIf="inChat()" [users]="users"></chat-messages>
        </div>
        <div class="four wide column">
          <chat-users *ngIf="inChat()" [users]="users"></chat-users>
        </div>
      </div>
    </div>
  `
})
export class ChatViewComponent extends BaseComponent implements OnInit, AfterViewInit {

  @ViewChild("chatGrid") chatGrid: ElementRef;

  chatId: string;
  users: Observable<User[]>;

  constructor(protected authenticationService: AuthenticationService,
              private chatService: ChatService,
              private userService: UserService,
              private route: ActivatedRoute) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(map => {
      this.chatId = map.get('chatId');
      this.users = this.getUsers();
    });
  }

  ngAfterViewInit(): void {
    this.updateViewSize();
  }

  @HostListener("window:resize")
  onResize(): void {
    this.updateViewSize()
  }

  updateViewSize(): void {
    let boundingRect = this.chatGrid.nativeElement.getBoundingClientRect();
    let styles: CSSStyleDeclaration = this.chatGrid.nativeElement.style;
    let height: number = document.documentElement.clientHeight - boundingRect.top;
    styles.height = `${height}px`;
  }

  inChat(): boolean {
    return !!this.chatId;
  }

  getUsers(): Observable<User[]> {
    return this.chatService.users(this.chatId)
      .flatMap(users => Observable.from(users))
      .flatMap(uid => this.userService.findById(uid))
      .reduce((acc, val) => acc.concat(val), []);
  }
}
