import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../models/user.model';
import { NotificationService } from './notification.service';
import { environment } from 'src/environments/environment';

@Injectable()
export class AuthService {

	localStorageKey = 'luxand_server_logged_user';

	constructor(private http: HttpClient, private notificationService: NotificationService, private router: Router) { }

	getLoggedUser(): User | undefined {
		const storedData = localStorage.getItem(this.localStorageKey);
		if (storedData) {
			return JSON.parse(storedData);
		}
		return undefined;
	}

	authenticate(formValue: any): Observable<User> {
		const url = `${environment.restServerUrl}/auth/login`;
		return this.http.post<User>(url, formValue);
	}

	clearSessionFromServer(): Observable<any> {
		return this.http.post<User>(`${environment.restServerUrl}/auth/logout`, null);
	}

	login(user: User): void {
		localStorage.setItem(this.localStorageKey, JSON.stringify(user));
		this.router.navigate(['/configurations']);
		this.notificationService.notify(`Usuário ${user.name} logado com sucesso!`);
	}

	logout(message: string): void {
		localStorage.removeItem(this.localStorageKey);
		this.toLoginPage();
		this.notificationService.notify(message);
	}

	clearSession(): void {
		localStorage.removeItem(this.localStorageKey);
		this.notificationService.notify('Sessão finalizada, entre novamente', 'warning');
	}

	isLoggedIn(): boolean {
		return this.getLoggedUser() !== undefined;
	}

	isSessionValid(): Observable<boolean> {
		return this.isLoggedIn() ?
			this.http.post<boolean>(`${environment.restServerUrl}/auth/istokenvalid`, this.getLoggedUser()?.accessToken)
			: of(false);
	}

	getUnitName(): Observable<any> {
		return this.http.get(`${environment.restServerUrl}/auth/getunitname`, { responseType: 'text' });
	}

	toLoginPage(): void {
		this.router.navigate(['/login']);
	}

	toHomePage(): void {
		this.router.navigate(['/configurations']);
	}

}
