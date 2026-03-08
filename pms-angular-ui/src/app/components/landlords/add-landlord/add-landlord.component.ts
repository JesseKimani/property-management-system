import { Component, OnInit } from '@angular/core';
import { LandlordService, Landlord } from '../../../services/landlord.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { identity } from 'rxjs';

@Component({
  selector: 'app-add-landlord',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './add-landlord.component.html',
  styleUrl: './add-landlord.component.css',
})

export class AddLandlordComponent {
  landlords: Landlord[] = [];
  landlordForm: FormGroup;
  selectedLandlord: Landlord | null = null;

  constructor(
    private landlordService: LandlordService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.landlordForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.email]],
      phone: ['', Validators.required],
      identity: [''],
      pin: [''],
    });
  }

  ngOnInit(): void {
    this.loadLandlords();
  }

  submitForm() {
    if (this.landlordForm.invalid) return;

    const landlordData: Landlord = this.landlordForm.value;
    
    if (this.selectedLandlord) {
      // Update landlord
      this.landlordService.update(this.selectedLandlord.id!, landlordData).subscribe(() => {
        this.loadLandlords();
        this.snackBar.open('Landlord updated successfully', 'Close', { duration: 3000 });
        this.resetForm();
      });
      
    } else {
      // Create landlord
      this.landlordService.create(landlordData).subscribe(() => {
        this.loadLandlords();
        this.snackBar.open('Landlord added successfully', 'Close', { duration: 3000 });
        this.resetForm();
      });
    }
  }

  loadLandlords() {
    this.landlordService.getAll().subscribe(data => {
      this.landlords = data;
    })
  }

  resetForm() {
    this.landlordForm.reset();
    this.selectedLandlord = null;
  }
}
