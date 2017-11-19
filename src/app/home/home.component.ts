import { Component, 
         OnInit, 
         ChangeDetectionStrategy, 
         ViewChild,
         ChangeDetectorRef } from '@angular/core';
import { Store } from '@ngrx/store';
import * as fromHome from './reducer';
import * as fromAction from './actions';
import * as fromRoot from '../reducers'
import { Observable } from 'rxjs/Observable';
import {GoogleMapService} from '../google-map.service'
import 'rxjs/add/operator/last';
import { HeatmapLayer } from '@ngui/map';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  showLoading$: Observable<boolean>;
  LocationCode$: Observable<any>;
  @ViewChild(HeatmapLayer) heatmapLayer: HeatmapLayer;
  heatmap: google.maps.visualization.HeatmapLayer;
  map: google.maps.Map;
  points = [];
  center: any;
  autocomplete: google.maps.places.Autocomplete;
  address: any = {};
  constructor(private store: Store<fromRoot.State>, private googleMapService: GoogleMapService, private ref: ChangeDetectorRef) {
    this.showLoading$ = this.store.select(fromRoot.getShowLoading);
   }
   
  initialized(autocomplete: any) {
    this.autocomplete = autocomplete;
  }
   loadLocation() {
    // Get all comments
      this.googleMapService.loadLocation()
        .subscribe(
          location => {
            this.LocationCode$ = location;
            this.center = location;
            console.log(this.LocationCode$)
            console.log('here');
          }, //Bind to view
          err => {
            // Log errors if any
            console.log(err);
        });
      }
  ngOnInit() {
    this.loadLocation();
    this.heatmapLayer['initialized$'].subscribe(heatmap => {
      this.points = [
        new google.maps.LatLng(6.660101, 3.351408),
        new google.maps.LatLng(6.660235, 3.351466),
        new google.maps.LatLng(6.660112, 3.351598)
      ];
      this.heatmap = heatmap;
      this.map = this.heatmap.getMap();
    });
  }
  toggleHeatmap() {
    this.heatmap.setMap(this.heatmap.getMap() ? null : this.map);
  }
  changeRadius() {
    this.heatmap.set('radius', this.heatmap.get('radius') ? null : 20);
  }
  loadRandomPoints() {
    this.points = [];

    for (let i = 0 ; i < 60; i++) {
      this.addPoint();
    }
  }
  addPoint() {
    let randomLat = Math.random() * 0.0999 + 6.601838;
    let randomLng = Math.random() * 0.0999 + 3.3514863;
    let latlng = new google.maps.LatLng(randomLat, randomLng);
    this.points.push(latlng);
  }
  placeChanged(place) {
    this.center = place.geometry.location;
    console.log(this.center.lat());
    let location = {
      lat: this.center.lat(),
      lng: this.center.lng(),
    }
    for (let i = 0; i < place.address_components.length; i++) {
      let addressType = place.address_components[i].types[0];
      this.address[addressType] = place.address_components[i].long_name;
    }
    this.ref.detectChanges();
  }
}
