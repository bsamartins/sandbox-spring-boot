import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {ChatViewComponent} from "./chat/chat.component";
import {MainViewComponent} from "./main-view.component";
import {HomeViewComponent} from "./home-view.component";
import {AuthenticationGuard} from "./authentication.guard";

@NgModule({
  imports: [
    RouterModule.forRoot([
      {
        path: '', redirectTo: '/home', pathMatch: 'full'
      },
      // { path: '403', component: NotAuthorisedComponent, pathMatch: 'full' },
      // { path: '404', component: NotFoundComponent },
      // { path: 'error', component: ErrorPageComponent, pathMatch: 'full' },
      {
        path: '', component: MainViewComponent,
        children: [
          { path: 'home', component: HomeViewComponent, pathMatch: 'full' },
        ]
      },
      {
        path: 'chats', component: ChatViewComponent,
        pathMatch: 'full', canActivate: [AuthenticationGuard]
      },
      {
        path: 'chats/:chatId', component: ChatViewComponent,
        pathMatch: 'full', canActivate: [AuthenticationGuard]
      },
      // { path: '**', component: NotFoundComponent }
    ], { enableTracing: true })
  ],
})
export class AppRoutingModule { }
