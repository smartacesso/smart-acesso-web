import { EventEmitter } from '@angular/core';

export class NotificationService {
	notificationEmitter = new EventEmitter<any>();

	notify(message: string, type: string = 'success'): void {
		this.notificationEmitter.emit({ message, type });
	}

}
