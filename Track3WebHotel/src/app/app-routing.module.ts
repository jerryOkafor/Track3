/**
 * Created by Oise on 21/03/2017.
 */

import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';

const appRoutes: Routes = [
    {path: '', component: AppComponent},
    {path: '', redirectTo: '/', pathMatch: 'full'}
];


@NgModule({
    imports: [
        RouterModule.forRoot(appRoutes)
    ],
    exports: [
        RouterModule
    ]
})

export class AppRoutingModule {

}
