import { Configuration } from '../models/configuration.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable()
export class ConfigurationService {

	constructor(private http: HttpClient){ }

	configurations(): Observable<Configuration[]> {
		return this.http.get<Configuration[]>(`${environment.restServerUrl}/configurations`);
	}

	saveConfigurations(formValue: any): Observable<Configuration[]> {
		return this.http.post<Configuration[]>(`${environment.restServerUrl}/configurations`, formValue);
	}

	saveTrackerBackup(): Observable<any> {
		return this.http.post(`${environment.restServerUrl}/configurations/savetrackerbackup`, '', { responseType: 'text' });
	}

}
