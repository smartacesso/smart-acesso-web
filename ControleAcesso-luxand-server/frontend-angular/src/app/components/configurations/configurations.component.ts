import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from 'src/app/services/notification.service';
import { Configuration } from 'src/app/models/configuration.model';
import { ConfigurationService } from 'src/app/services/configuration.service';

@Component({
	selector: 'app-configurations',
	templateUrl: './configurations.component.html'
})
export class ConfigurationsComponent implements OnInit, AfterViewInit {

	configurationsForm!: FormGroup;

	configurations!: Configuration[];

	constructor(private formBuilder: FormBuilder,
		private configurationService: ConfigurationService,
		private notificationService: NotificationService) { }

	ngOnInit(): void {
	}

	ngAfterViewInit(): void {
		this.configurationService.configurations()
			.subscribe(configurations => {
				this.configurations = configurations;
				this.configurationsForm = this.formBuilder.group({});
				this.configurations.forEach(configuration => {
					if (configuration.type === 'BOOLEAN') {
						this.configurationsForm.addControl(configuration.name,
							this.formBuilder.control(configuration.value === 'true' ? true : false, Validators.required));
					}
					else {
						this.configurationsForm.addControl(configuration.name, this.formBuilder.control(configuration.value, Validators.required));
					}
				});
			});
	}

	isInvalidField(formControlName: string): boolean | undefined {
		const f = this.configurationsForm?.controls[formControlName];
		return f?.invalid && (f.touched || (f.dirty && f.touched));
	}

	getValidationErrorMessage(formControlName: string): string | undefined {
		const controlErrors = this.configurationsForm?.get(formControlName)?.errors;
		if (controlErrors) {
			if (Object.keys(controlErrors)[0] === 'required') {
				return `Campo obrigatório`;
			}
			return Object.keys(controlErrors)[0];
		}
		return undefined;
	}

	save(): void {
		for (const configuration of this.configurations) {
			configuration.value = this.configurationsForm.controls[configuration.name].value;
		}

		this.configurationService.saveConfigurations(this.configurations)
			.subscribe(
				configurations => { // callback de sucesso
					this.configurations = configurations;
					this.notificationService.notify('Configurações salvas!');
				},
				response => { // HttpErrorResponse, callback de erro
					console.log(response);
					this.notificationService.notify(response.error.message, 'error');
				});
	}

	defaultValues(): void {
		this.configurations.forEach(configuration => {
			if (configuration.type === 'BOOLEAN') {
				this.configurationsForm?.controls[configuration.name].setValue(configuration.defaultValue === 'true' ? true : false);
			}
			else {
				this.configurationsForm?.controls[configuration.name].setValue(configuration.defaultValue);
			}
		});
	}

	saveTrackerBackup(): void {
		this.configurationService.saveTrackerBackup()
			.subscribe(
				message => { // callback de sucesso
					if (message === '') {
						this.notificationService.notify('Backup do Tracker salvo!');
					}
					else {
						this.notificationService.notify(message, 'error');
					}
				},
				response => { // HttpErrorResponse, callback de erro
					console.log(response);
					this.notificationService.notify(response.error.message, 'error');
				});
	}

}
