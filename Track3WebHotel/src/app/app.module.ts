import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { APP_CONFIG, appConfig } from './app.config';
import { DataService } from './shared/services/data.service';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from './shared/shared.module';
import { CheckInComponent } from './check-in/check-in.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularFireModule } from 'angularfire2';
import { environment } from '../environments/environment';
import { AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabaseModule } from 'angularfire2/database';


@NgModule({
    declarations: [
        AppComponent,
        CheckInComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        AngularFireModule.initializeApp(environment.firebase, 'test-app'),
        AngularFireAuthModule,
        AngularFireDatabaseModule
    ],
    providers: [
        DataService,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    bootstrap: [AppComponent]

})
export class AppModule {
}
