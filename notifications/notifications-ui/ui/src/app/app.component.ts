import {Component, NgZone, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs/Rx";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  messages: Message[] = [];

  constructor(private http: Http, private ngZone: NgZone) {

  }

  ngOnInit(): void {
    this.sse('/api/messages').map(m => JSON.parse(m.data) as Message).subscribe((d) => {
      console.log(d);
      this.messages.push(d);
    }, err => {
      console.log(err);
    });
  }

  sse(url): Observable<any> {
    return Observable.create(observer => {
      const sse = new window['EventSource'](url);

      sse.onmessage = (x) => {
        this.ngZone.run(() => observer.next(x));
      };

      sse.onerror = (x) => {
        console.error('EventSource failed', x);
        this.ngZone.run(() => observer.error(x));
      };

      return () => sse.close();
    });
  }

  submit(form: NgForm) {
    this.http.post('/api/messages', form.value.message).subscribe(() => {
      form.reset();
    }, err => {
      console.error(err);
    });
  }
}

export interface Message {
  content: string;
  timestamp: number;
}
