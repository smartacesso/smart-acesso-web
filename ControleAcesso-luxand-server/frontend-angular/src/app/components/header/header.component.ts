import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
	selector: 'app-header',
	templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit {

	constructor(public authService: AuthService, private notificationService: NotificationService) { }

	ngOnInit(): void {
	}

	logout(): void {
		if (window.confirm('Deseja realmente sair?')) {
			this.authService.clearSessionFromServer()
				.subscribe(
					() => {
						this.authService.logout('Sessão finalizada e dados limpos com sucesso!');
					},
					response => { // HttpErrorResponse
						console.log(response);
						this.notificationService.notify(response.error.message, 'error');
					});
		}
	}

	finalize_session(): void {
		if (window.confirm('Deseja realmente sair?')) {
			this.authService.logout('Sessão finalizada com sucesso!')
		}
	}

}
