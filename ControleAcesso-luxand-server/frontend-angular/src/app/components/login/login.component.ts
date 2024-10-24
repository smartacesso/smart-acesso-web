import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

	loginForm!: FormGroup;

	unitname!: string;

	constructor(private formBuilder: FormBuilder, private authService: AuthService, private notificationService: NotificationService) { }

	ngOnInit(): void {
		this.authService.getUnitName()
			.subscribe(unitname => {
				this.unitname = unitname;
				this.loginForm = this.formBuilder.group({
					unitname: this.formBuilder.control(this.unitname, [Validators.required]),
					username: this.formBuilder.control('', [Validators.required]),
					password: this.formBuilder.control('', [Validators.required]),
				});
			});
	}

	isInvalidField(formControlName: string): boolean | undefined {
		const f = this.loginForm?.controls[formControlName];
		return f?.invalid && (f.touched || (f.dirty && f.touched));
	}

	login(): void {
		this.authService.authenticate(this.loginForm.value)
			.subscribe(
				user => { // callback de sucesso
					this.authService.login(user);
				},
				response => { // HttpErrorResponse, callback de erro
					this.notificationService.notify(response.error.message, 'error');
				});
	}

}
