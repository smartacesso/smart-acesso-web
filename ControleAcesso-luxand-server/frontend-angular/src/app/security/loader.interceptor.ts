import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { LoaderService } from '../services/loader.service';

@Injectable()
export class LoaderInterceptor implements HttpInterceptor {

	private count = 0;

	constructor(public loaderService: LoaderService) { }

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

		if (this.count === 0) {
			this.loaderService.show();
		}

		this.count++;

		return next.handle(req).pipe(
			finalize(() => {
				this.count--;
				if (this.count === 0) {
					this.loaderService.hide();
				}
			}));
	}
}
