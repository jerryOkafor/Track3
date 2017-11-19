import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'zd-check-in',
    templateUrl: './check-in.component.html',
    styleUrls: ['./check-in.component.scss']
})
export class CheckInComponent implements OnInit {

    checkInForm: FormGroup;

    isSubmitted: boolean;

    constructor(private fb: FormBuilder) {

    }

    ngOnInit() {

        // Instantiate Reactive Form
        this.checkInForm = this.fb.group({
            id: ['', Validators.required],
            title: ['', Validators.required],
            description: [''],
            status: ['', [Validators.required]]
        });
    }


    checkIn() {
        this.isSubmitted = true;
        if (this.checkInForm.valid) {
        }

    }


}
