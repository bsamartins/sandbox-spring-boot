import {NgModule} from "@angular/core";
import {AuthenticationService} from "./authentication.service";
import {Sse} from "./sse";
import {AuthModule} from "./auth.module";
import {MessageService} from "./message.service";
import {HttpModule} from "@angular/http";
import {UserService} from "./user.service";
import {ChatService} from "./chat.service";

@NgModule({
  declarations: [],
  imports: [HttpModule, AuthModule],
  providers: [
    AuthenticationService,
    Sse,
    MessageService,
    UserService,
    ChatService
  ]
})
export class ServicesModule { }
