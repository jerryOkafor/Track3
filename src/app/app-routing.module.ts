import { NgModule }                     from '@angular/core';
// import { AuthGuard }                    from './guards/auth.guard';
import { RouterModule, Routes }         from '@angular/router';
import { HomeComponent }                from './home/home.component';

//import { HeroDetailComponent }  from './hero-detail.component';
const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home',  component: HomeComponent },
  { path: '**', redirectTo: '' }
];
@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
