import {Component, Input} from '@angular/core';

@Component({
  selector: 'modal',
  template: `    
    <div class="ui {{modalSize}} modal" [class.active]="active">
      <ng-content></ng-content>
    </div>
  `
})
export class ModalComponent {

  @Input() modalSize: string;

  private active: boolean;

  show(): void {
    this.active = true;
  }

  hide(): void {
    this.active = false;
  }
}
