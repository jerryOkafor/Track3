import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {RouterModule } from '@angular/router';
import { EffectsModule } from '@ngrx/effects';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';
import { AppRoutingModule } from './app-routing.module';
import { reducers, metaReducers } from './reducers';
import { StoreModule } from '@ngrx/store';
import { GoogleMapService } from './google-map.service'
import { HomeEffects } from './home/effects' 
import { NguiMapModule } from '@ngui/map';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    StoreModule.forRoot(reducers, { metaReducers }),
    EffectsModule.forRoot([HomeEffects]),
    HttpModule,
    NguiMapModule.forRoot({apiUrl: 'https://maps.google.com/maps/api/js?key=AIzaSyBtTf4dR4wm6bg_r5ImHT7rR9OYeY8grIQ'})
  ],
  providers: [GoogleMapService],
  bootstrap: [AppComponent]
})
export class AppModule { }
