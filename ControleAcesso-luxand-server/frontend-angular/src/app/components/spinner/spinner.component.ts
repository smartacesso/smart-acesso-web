import { Component, OnInit } from '@angular/core';
import { LoaderService } from 'src/app/services/loader.service';
import { Subject } from 'rxjs';

@Component({
	selector: 'app-spinner',
	templateUrl: './spinner.component.html',
	styleUrls: ['./spinner.component.css']
})
export class SpinnerComponent implements OnInit {

	isLoading: Subject<boolean> = this.loaderService.isLoading;

	constructor(private loaderService: LoaderService) { }

	ngOnInit(): void {
	}

}
