import {AfterViewInit, Component, ElementRef, HostListener, ViewChild} from '@angular/core';
import {BaseComponent} from "../base.component";
import {AuthenticationService} from "../services/authentication.service";
import {ModalComponent} from "../modal/modal.component";

@Component({
  selector: 'chat-view',
  styleUrls: ['./chat-view.component.scss'],
  template: `
    <div #chatGrid class="ui padded grid">
      <div class="row">
        <div class="four wide column">
          <chat-list></chat-list>
        </div>
        <div class="messages eight wide column">
          <messages-view></messages-view>
        </div>
        <div class="four wide olive column">
          Olive
        </div>
      </div>
    </div>
  `
})
export class ChatViewComponent extends BaseComponent implements AfterViewInit {

  @ViewChild("chatGrid") chatGrid: ElementRef;

  constructor(protected authenticationService: AuthenticationService) {
    super(authenticationService);
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
}
