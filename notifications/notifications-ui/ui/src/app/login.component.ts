import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "./services/authentication.service";
import {BaseComponent} from "./base.component";

@Component({
  selector: 'login',
  styleUrls: ['./login.component.scss'],
  template: `
    <div class="ui mini input">
      <form [formGroup]="loginForm" (key.enter)="login()">
        <input type="text" id="username"
               formControlName="username"
               class="spacing" 
               placeholder="Username"/>
        <input type="password" id="password"
               formControlName="password"
               class="spacing" 
               placeholder="Password"/>
      </form>
      <button class="ui primary right labeled icon mini button" 
              [class.loading]="loading" 
              [disabled]="loading || (loginForm.errors)" 
              (click)="login()">Login<i class="right power icon"></i></button>
    </div>
  `
})
export class LoginComponent extends BaseComponent implements OnInit{
  loading: boolean;
  loginForm: FormGroup;

  constructor(protected authenticationService: AuthenticationService) {
    super(authenticationService);
  }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    })
  }

  login(): void {

    if(this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.loginForm.disable();

    let username: string = this.loginForm.controls['username'].value as string;
    let password: string = this.loginForm.controls['password'].value as string;

    this.authenticationService.authenticate(username, password).subscribe(token => {
      this.loading = false;
      this.loginForm.enable();

      console.log('successful login', token);
    }, err => {
      this.loading = false;
      this.loginForm.enable();

      console.error('login error', err);
    })
  }
}
