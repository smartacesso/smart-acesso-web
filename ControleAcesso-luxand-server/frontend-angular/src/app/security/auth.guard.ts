import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthGuard implements CanActivate {

	constructor(private authService: AuthService) { }

	canActivate(activatedRoute: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): boolean {
		if (activatedRoute.url[0].path === 'login') {
			if (this.authService.isLoggedIn()) {
				this.authService.isSessionValid().subscribe(isSessionValid => {
					if (isSessionValid) {
						this.authService.toHomePage();
					}
					else {
						this.authService.clearSession();
					}
				});
			}
		}
		else {
			if (!this.authService.isLoggedIn()) {
				this.authService.toLoginPage();
			}
			else {
				this.authService.isSessionValid().subscribe(isSessionValid => {
					if (!isSessionValid) {
						this.authService.clearSession();
						this.authService.toLoginPage();
					}
				});
			}
		}
		return true;
	}

}
