import {Component, OnInit, ViewChild} from '@angular/core';
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
      <div *ngFor="let group of groups" class="item">
        <img class="ui avatar image" src="{{ group.pictureUri }}">
        <div class="content">
          <div class="header">{{ group.name }}</div>
        </div>
      </div>
    </div>
  `
})
export class ChatListComponent extends BaseComponent implements OnInit {
  @ViewChild("createModal") createModal: ModalComponent;
  groups: Chat[] = [];

  constructor(private authenticationService: AuthenticationService,
              private chatService: ChatService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.chatService.find().subscribe(d => {
      this.groups = d;
    });
  }

  create(): void {
    this.createModal.show();
  }

  onCloseCreateModal(event): void {
    if(event){
      this.chatService.find().subscribe(d => {
        this.groups = d;
      });
    }
    this.createModal.hide();
  }

  joinChat(chat: Chat): void {
    this.chatService.join(chat)
      .flatMap(x => this.chatService.find())
      .subscribe(result => {
        this.groups = result;
      });
  }
}
