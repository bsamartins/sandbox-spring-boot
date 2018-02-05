import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {NavbarComponent} from "./navbar.component";
import {LoginComponent} from "./login.component";
import {ServicesModule} from "./services/services.module";
import {AuthModule} from "./services/auth.module";
import {MessagesViewComponent} from "./chat/messages-view.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppRoutingModule} from "./app-routing.module";
import {HomeViewComponent} from "./home-view.component";
import {ChatViewComponent} from "./chat/chat-view.component";
import {RouterModule} from "@angular/router";
import {MainViewComponent} from "./main-view.component";
import {AuthenticationGuard} from "./authentication.guard";
import {ChatGroupsListComponent} from "./chat/chatgroups-list.component";
import {ModalComponent} from "./modal/modal.component";
import {ChatGroupsCreateComponent} from "./chat/chatgroups-create.component";

@NgModule({
  declarations: [
    AppComponent,

    ModalComponent,

    HomeViewComponent,
    NavbarComponent,
    LoginComponent,

    MainViewComponent,

    ChatViewComponent,
    ChatGroupsListComponent,
    ChatGroupsCreateComponent,
    MessagesViewComponent
  ],
  imports: [
    RouterModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
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
