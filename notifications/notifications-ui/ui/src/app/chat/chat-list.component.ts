import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../base.component";
import {AuthenticationService} from "../services/authentication.service";
import {ModalComponent} from "../modal/modal.component";
import {ChatService} from "../services/chat.service";
import {Chat} from "../services/types";

@Component({
  selector: 'chat-list',
  styleUrls: ['./chat-list.component.scss'],
  template: `
    <modal #createModal modalSize="mini">
      <div class="content">
        <chat-create (close)="onCloseCreateModal($event)"></chat-create>
      </div>
    </modal>
    <div class="search-section">
      <div class="full-width item">
        <chat-search (select)="joinChat($event)"></chat-search>
      </div>
      <div class="item">
        <button class="ui circular icon button" (click)="create()">
          <i class="icon add"></i>
        </button>
      </div>
    </div>
    <div class="ui middle aligned selection list">
      <a *ngFor="let chat of chats" class="item" [routerLink]="['/chats', chat.id]" 
         [class.active]="isActive(chat)">
        <img class="ui avatar image" src="{{ chat.pictureUri }}">
        <div class="content">
          <div class="header">{{ chat.name }}</div>
        </div>
      </a>
    </div>
  `
})
export class ChatListComponent extends BaseComponent implements OnInit {

  @ViewChild("createModal") createModal: ModalComponent;

  @Input() activeChatId: string;

  chats: Chat[] = [];

  constructor(private authenticationService: AuthenticationService,
              private chatService: ChatService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.chatService.find().subscribe(d => {
      this.chats = d;
    });
  }

  create(): void {
    this.createModal.show();
  }

  onCloseCreateModal(event): void {
    if(event){
      this.chatService.find().subscribe(d => {
        this.chats = d;
      });
    }
    this.createModal.hide();
  }

  joinChat(chat: Chat): void {
    this.chatService.join(chat.id)
      .flatMap(x => this.chatService.find())
      .subscribe(result => {
        this.chats = result;
      });
  }

  isActive(chat: Chat): boolean {
    return chat.id == this.activeChatId;
  }
}
