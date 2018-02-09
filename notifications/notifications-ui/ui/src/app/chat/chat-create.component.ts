import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Chat, ChatCreate} from "../services/types";
import {ChatService} from "../services/chat.service";

@Component({
  selector: 'chat-create',
  styleUrls: ['./chat-create.component.scss'],
  template: `
    <form class="ui form" [formGroup]="form">
      <div class="ui inverted dimmer" [class.active]="busy"></div>
      <div class="fields">
        <div class="three wide field">
          <label>Icon</label>
          <div *ngIf="!hasImage()" class="ui icon-placeholder group-icon circular image" (click)="openFile()">
            <span>Select</span>
          </div>
          <img *ngIf="hasImage()" class="ui group-icon circular image" 
               [src]="imageUrl" 
               (click)="openFile()">
          <input #fileInput type="file" style="display: none" (change)="onFileSelected($event)"/>
        </div>
        <div class="thirteen wide field">
          <label>Name</label>
          <input type="text" name="name" placeholder="Name" formControlName="name">
        </div>
      </div>
      <button class="ui button" (click)="cancel()">Cancel</button>
      <button class="ui button" (click)="save()" [disabled]="form.invalid">Create</button>
    </form>
  `
})
export class ChatCreateComponent {

  @ViewChild("fileInput") fileInput: ElementRef;

  @Output() close: EventEmitter<Chat> = new EventEmitter();

  form: FormGroup;
  imageUrl: SafeUrl;
  imageFile: File;
  busy: boolean;

  constructor(private domSanitizer: DomSanitizer,
              private groupService: ChatService) {
    this.form = new FormGroup({
      'image': new FormControl(null, Validators.required),
      'name': new FormControl(null, [Validators.required])
    });
  }

  save(): void {
    let imageString: string = this.form.controls['image'].value;
    let group: ChatCreate = {
      name: this.form.controls['name'].value,
      picture: {
        name: this.imageFile.name,
        content: btoa(imageString),
        mediaType: this.imageFile.type
      }
    };

    this.busy = true;
    this.groupService.create(group).subscribe(d => {
      console.log('success', d);
      this.close.emit(d);
      this.busy = false;
    }, err => {
      console.error('error', err);
      this.busy = false;
    });

  }

  cancel(): void {
    this.close.emit(null);
  }

  hasImage(): boolean {
    return !!this.imageUrl;
  }

  private onFileSelected() {
    let el: HTMLInputElement = this.fileInput.nativeElement;
    if(el.files.length > 0) {
      let f: File = el.files[0];
      this.showImage(f);
    }
    console.log(el.files);
  }

  private showImage(file: File): void {
    let reader: FileReader = new FileReader();
    reader.onload = () => {
      let content: string = btoa(reader.result);
      this.imageUrl = this.domSanitizer.bypassSecurityTrustUrl(`data:image/png;base64, ${content}`);
      this.form.controls['image'].setValue(reader.result);
      this.imageFile = file;
    };

    reader.onloadend = () => {
      this.busy = false;
    };

    this.busy = true;
    reader.readAsBinaryString(file);
  }

  openFile(): void {
    this.fileInput.nativeElement.click();
  }
}
