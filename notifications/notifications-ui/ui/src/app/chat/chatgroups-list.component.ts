import {Component, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../base.component";
import {AuthenticationService} from "../services/authentication.service";
import {ModalComponent} from "../modal/modal.component";
import {GroupService} from "../services/group.service";
import {Group} from "../services/types";

@Component({
  selector: 'chatgroups-list',
  styleUrls: ['./chatgroups-list.component.scss'],
  template: `
    <modal #createModal modalSize="mini">       
      <div class="content">
        <chatgroups-create (close)="onCloseCreateModal($event)"></chatgroups-create>
      </div>
    </modal>
    <div class="ui">
      <button class="ui button" (click)="create()">New...</button>
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
export class ChatGroupsListComponent extends BaseComponent implements OnInit {

  @ViewChild("createModal") createModal: ModalComponent;
  groups: Group[] = [];


  constructor(private authenticationService: AuthenticationService,
              private groupService: GroupService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.groupService.find().subscribe(d => {
      this.groups = d;
    });
  }

  create(): void {
    this.createModal.show();
  }

  onCloseCreateModal(event): void {
    if(event){
      this.groupService.find().subscribe(d => {
        this.groups = d;
      });
    }
    this.createModal.hide();
  }
}
