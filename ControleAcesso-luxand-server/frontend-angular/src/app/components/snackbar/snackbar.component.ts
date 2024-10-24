import { Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { NotificationService } from 'src/app/services/notification.service';
import { timer } from 'rxjs';
import { tap, switchMap } from 'rxjs/operators';

@Component({
	selector: 'app-snackbar',
	templateUrl: './snackbar.component.html',
	animations: [
		trigger('snack-visibility', [
			state('hidden', style({
				opacity: 0,
				top: '-60px'
			})),
			state('visible', style({
				opacity: 0.8,
				top: '65px'
			})),
			transition('void => hidden', animate('0ms')),
			transition('hidden => visible', animate('500ms 0s ease-in')),
			transition('visible => hidden', animate('500ms 0s ease-out'))
		])
	]
})
export class SnackbarComponent implements OnInit {

	message = '';
	type = '';
	state = 'hidden';

	constructor(private notificationService: NotificationService) { }

	ngOnInit(): void {
		this.notificationService.notificationEmitter.pipe(
			tap((obj: any) => {

				this.message = obj.message;
				this.type = obj.type;
				this.state = 'visible';

			}),
			switchMap(() => timer(3000))
		).subscribe(() => this.state = 'hidden');
	}

}
