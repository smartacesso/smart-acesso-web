import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { RootComponent } from './components/root/root.component';
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { ConfigurationsComponent } from './components/configurations/configurations.component';
import { SnackbarComponent } from './components/snackbar/snackbar.component';
import { AuthService } from './services/auth.service';
import { NotificationService } from './services/notification.service';
import { ConfigurationService } from './services/configuration.service';
import { LoaderService } from './services/loader.service';
import { AuthGuard } from './security/auth.guard';
import { LoaderInterceptor } from './security/loader.interceptor';
import { AuthInterceptor } from './security/auth.interceptor';
import { ApplicationErrorHandler } from './utils/app.error-handler';
import { NotFoundComponent } from './components/not-found/not-found.component';

@NgModule({
	declarations: [
		RootComponent,
		HeaderComponent,
		LoginComponent,
		SpinnerComponent,
		SnackbarComponent,
		ConfigurationsComponent,
		NotFoundComponent
	],
	imports: [
		BrowserModule,
		FormsModule,
		AppRoutingModule,
		ReactiveFormsModule,
		HttpClientModule,
		BrowserAnimationsModule
	],
	providers: [
		AuthService,
		NotificationService,
		ConfigurationService,
		LoaderService,
		AuthGuard,
		{ provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true },
		{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
		{ provide: ErrorHandler, useClass: ApplicationErrorHandler }
	],
	bootstrap: [
		RootComponent
	]
})
export class AppModule { }
