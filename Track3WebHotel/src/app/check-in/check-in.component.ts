import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { generateRandomPoint } from 'generate-random-points';
import { DataService } from '../shared/services/data.service';


@Component({
    selector: 'zd-check-in',
    templateUrl: './check-in.component.html',
    styleUrls: ['./check-in.component.scss']
})
export class CheckInComponent implements OnInit {

    checkInForm: FormGroup;

    isSubmitted: boolean;

    isProcessing: boolean;

    checkInCompleted: boolean;

    constructor(private fb: FormBuilder, private dataService: DataService) {

    }


    ngOnInit() {

        this.isSubmitted = false;
        this.isProcessing = false;
        this.checkInCompleted = false;

        // Instantiate Reactive Form
        this.checkInForm = this.fb.group({
            fullName: ['', Validators.required],
            location: [''],
            locationName: [''],
            locationAddress: [''],
            id: ['', Validators.required],
            email: ['']
        });
    }

    /**
     * Check in a tourist or visitor
     */
    checkIn() {
        this.isSubmitted = true;
        if (this.checkInForm.valid) {
            this.isProcessing = true;
            const randLocation = this.generateRandomLocation();

            //Transform JSON Object
            let body = this.checkInForm.value;
            body.location = {
                lat: randLocation.latitude,
                lng: randLocation.longitude
            };

            this.dataService.post('https://sdg-track-3.firebaseio.com/people.json', body).subscribe(
                (data) => {
                    this.checkInCompleted = true;
                    this.resetForm();
                },
                error => {
                    this.isProcessing = false;
                    console.log(error);
                })
        }

    }


    resetForm() {
        setTimeout(() => {
            this.checkInForm.reset();
            this.isProcessing = false;
            this.isSubmitted = false;
        }, 5000)
    }

    /**
     * Generate a random
     * @returns {any}
     */
    generateRandomLocation() {
        const options = {
            centerPosition: {
                latitude: 6.5244,
                longitude: 3.3792
            },
            radius: 1000
        }


        return generateRandomPoint(options.centerPosition, options.radius);

    }


}
