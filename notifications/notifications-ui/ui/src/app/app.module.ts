import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {NavbarComponent} from "./navbar.component";
import {LoginComponent} from "./login.component";
import {ServicesModule} from "./services/services.module";
import {AuthModule} from "./services/auth.module";
import {ChatMessagesComponent} from "./chat/chat-messages.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppRoutingModule} from "./app-routing.module";
import {HomeViewComponent} from "./home-view.component";
import {ChatViewComponent} from "./chat/chat.component";
import {RouterModule} from "@angular/router";
import {MainViewComponent} from "./main-view.component";
import {AuthenticationGuard} from "./authentication.guard";
import {ChatListComponent} from "./chat/chat-list.component";
import {ModalComponent} from "./modal/modal.component";
import {ChatCreateComponent} from "./chat/chat-create.component";
import {ChatSearchComponent} from "./chat/chat-search.component";
import {ClickOutsideModule} from 'ng-click-outside';
import {ChatUsersComponent} from "./chat/chat-users.component";

@NgModule({
  declarations: [
    AppComponent,

    ModalComponent,

    HomeViewComponent,
    NavbarComponent,
    LoginComponent,

    MainViewComponent,

    ChatViewComponent,
    ChatListComponent,
    ChatCreateComponent,
    ChatSearchComponent,
    ChatMessagesComponent,
    ChatUsersComponent
  ],
  imports: [
    RouterModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    ClickOutsideModule,
    AppRoutingModule,
    AuthModule,
    ServicesModule
  ],
  providers: [
    AuthenticationGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
