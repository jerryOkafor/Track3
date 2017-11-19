import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/retryWhen';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/take';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class DataService {
    private headers: HttpHeaders = new HttpHeaders();

    private token = localStorage.getItem('token');

    constructor(private http: HttpClient) {
        this.headers.set('Authorization', `Bearer ${this.token}`);
    }

    private retryCondition(errors: Observable<any>) {
        return errors.mergeMap((error) => (error.status === 500) ? Observable.throw(error) : Observable.of(error))
            .delay(1000)
            .take(2);
    }

    get(url): Observable<any> {
        return this.http.get(url, {headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)})
            .catch(this.handleError);

    }

    post(url, body): Observable<any> {
        return this.http.post(url, body, {headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)});
    }

    handleError(error: HttpErrorResponse) {
        console.log(error);
        return Observable.throw(error);
    }

}

