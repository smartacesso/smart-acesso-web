import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { ConfigurationsComponent } from './components/configurations/configurations.component';
import { AuthGuard } from './security/auth.guard';
import { NotFoundComponent } from './components/not-found/not-found.component';

const routes: Routes = [
	{ path: '', redirectTo: 'configurations', pathMatch: 'full' },
	{ path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
	{ path: 'configurations', component: ConfigurationsComponent, canActivate: [AuthGuard] },
	{ path: '404', component: NotFoundComponent },
	{ path: '**', redirectTo: '404', pathMatch: 'full' }
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
