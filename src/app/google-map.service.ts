import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Store} from '@ngrx/store';  
import {Observable} from 'rxjs/Observable';  
// import {AppState} from '../models/app-state';
import * as fromRoot from './reducers';

@Injectable()
export class GoogleMapService {
  private API_PATH = 'https://maps.googleapis.com/maps/api/geocode/json?address= query&key=YOUR_API_KEY' ;
  constructor(private http: Http, private store: Store<fromRoot.State>) {}
  
    getLocation(query): Observable<any> {
      return this.http.get('https://maps.googleapis.com/maps/api/geocode/json?address=' + query)
        .map(res => res.json() || []);
    } 
    loadLocation() : Observable<any> {
      return this.store.select(fromRoot.getLocationCode)
    }
}
