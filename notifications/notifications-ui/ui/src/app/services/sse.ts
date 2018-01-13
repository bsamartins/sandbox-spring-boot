import {Injectable, NgZone} from "@angular/core";
import {Observable} from "rxjs/Rx";
import {Subscriber} from "rxjs/src/Subscriber";
import EventSource from 'event-source';

@Injectable()
export class Sse {

  constructor(private ngZone: NgZone) {
  }

  create(url: string, options?: any): Observable<any> {
    return Observable.create(observer => {
      let opts = options || {};
      let sse = this.initSseObservable(url, opts, observer);
      return () => sse.close();
    }).catch(error => {
      return Observable.throw(error);
    });
  }

  private initSseObservable(url: string, opts: any, observer: Subscriber<any>): any {
    let sse: EventSource = new EventSource(url, opts);
    sse.onmessage = (x) => {
      this.ngZone.run(() => observer.next(x));
    };

    sse.onerror = (x) => {
      console.error('EventSource failed', x);
      this.ngZone.run(() => observer.error(x));
    };

    return sse;
  }
}
