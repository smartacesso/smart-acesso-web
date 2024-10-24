import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandler, Injectable, NgZone } from '@angular/core';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class ApplicationErrorHandler extends ErrorHandler {

	constructor(private notificationService: NotificationService, private zone: NgZone) {
		super();
	}

	handleError(errorResponse: HttpErrorResponse | any): void {

		console.log(errorResponse);

		if (errorResponse instanceof HttpErrorResponse) {

			this.zone.run(() => {
				switch (errorResponse.status) {
					case 0:
						this.notificationService.notify('Não foi possível se conectar ao servidor.', 'error');
						break;

					case 401:
						this.notificationService.notify('Não autorizado', 'error');
						break;

					case 403:
						this.notificationService.notify('Não autorizado', 'error');
						break;

					case 404:
						this.notificationService.notify('Recurso não encontrado', 'error');
						break;

					case 500:
						this.notificationService.notify('Erro ao processar requisição', 'error');
						break;

					default:
						this.notificationService.notify(errorResponse.error?.message || errorResponse.message, 'error');
						break;
				}
			});

		}

	}
}
