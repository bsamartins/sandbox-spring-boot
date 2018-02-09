import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {Chat} from "../services/types";
import {ChatService} from "../services/chat.service";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'chat-search',
  template: `
    <div class="ui fluid search focus">
      <div class="ui fluid icon input">
        <i class="search icon"></i>
        <input #searchInput 
               class="prompt" type="text" placeholder="Search..." 
               (focus)="onFocusSearchInput()" 
               (keyup)="onChangeSearch($event)" 
               [(ngModel)]="searchQuery">
      </div>
      <div *ngIf="showSearchResults" class="results transition visible">
        <a *ngFor="let chat of chatSearchResults" class="result" (clickOutside)="onClickOutside($event)">
          <div class="content" (click)="onSelect(chat)">{{ chat.name }}</div>
        </a>
      </div>
    </div>
  `
})
export class ChatSearchComponent {

  @ViewChild("searchInput") searchInput: ElementRef;

  searchQuery: string;
  searchQueryEmitter: EventEmitter<string> = new EventEmitter();
  chatSearchResults: Chat[] = [];
  showSearchResults: boolean = false;

  @Output() select: EventEmitter<Chat> = new EventEmitter();

  constructor(private chatService: ChatService) {
    this.searchQueryEmitter
      .do(v => console.log(v))
      .distinctUntilChanged()
      .debounceTime(500)
      .flatMap(q => {
        if(q) {
          return this.chatService.search(q)
        } else {
          this.chatSearchResults = [];
          this.showSearchResults = false;
          return Observable.empty();
        }
      }).subscribe(x => {
      let results : Chat[] = x as Chat[];
      this.chatSearchResults = results;
      if(results.length > 0) {
        this.showSearchResults = true;
      }
    });
  }

  onFocusSearchInput(event: Event): void {
    if(this.chatSearchResults.length > 0) {
      this.showSearchResults = true;
    }
  }

  onChangeSearch(): void {
    this.searchQueryEmitter.emit(this.searchQuery)
  }

  onClickOutside(event: Event): void {
    if(event.target != this.searchInput.nativeElement) {
      this.showSearchResults = false;
    }
  }

  onSelect(chat: Chat): void {
    this.showSearchResults = false;
    this.select.emit(chat);
  }
}
