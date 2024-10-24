import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

	constructor(private authService: AuthService) { }

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

		if (this.authService.isLoggedIn() && !request.url.includes('auth')) {

			return this.authService.isSessionValid()
				.pipe(
					switchMap(isSessionValid => {

						if (isSessionValid) {
							const authRequest = request.clone({
								setHeaders: {
									Authorization: `Bearer ${this.authService.getLoggedUser()?.accessToken}`
								}
							});

							return next.handle(authRequest);
						}
						else {
							this.authService.clearSession();
							this.authService.toLoginPage();

							return EMPTY;
						}
					})
				);

		}

		return next.handle(request);

	}
}
