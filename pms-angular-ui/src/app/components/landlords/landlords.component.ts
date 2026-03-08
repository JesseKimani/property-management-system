import { Component, OnInit } from '@angular/core';
import { LandlordService, Landlord } from '../../services/landlord.service';
import { PropertyService } from '../../services/property-service.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { SelectionModel } from '@angular/cdk/collections';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-landlord',
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
    MatSnackBarModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  templateUrl: './landlords.component.html',
  styleUrls: ['./landlords.component.css']
})

export class LandlordComponent implements OnInit {
  // All landlords from the database
  allLandlords: Landlord[] = [];
  // Landlords displayed in the current page
  displayedLandlords: Landlord[] = [];
  landlordForm: FormGroup;
  selectedLandlord: Landlord | null = null;
  
  // Pagination properties
  pageSize = 10;
  pageSizeOptions: number[] = [5, 10, 25, 50];
  currentPage = 0;
  totalLandlords = 0;
  
  // Properties counts map
  propertyCounts: Map<number, number> = new Map();
  
  // Loading state
  isLoading = false;
  
  // Column definitions
  displayedColumns: string[] = ['select', 'number', 'name', 'email', 'phone', 'identity', 'pin', 'properties'];
  
  // Selection model for checkboxes
  selection = new SelectionModel<Landlord>(true, []);

  constructor(
    private landlordService: LandlordService,
    private propertyService: PropertyService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router
    
  ) {
    this.landlordForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      identity: ['', Validators.required],
      pin: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadLandlords();
  }

  loadLandlords() {
    this.isLoading = true;
    
    // First get all landlords
    this.landlordService.getAll().pipe(
      switchMap(landlords => {
        this.allLandlords = landlords;
        this.totalLandlords = landlords.length;
        
        // If there are no landlords, skip property loading
        if (landlords.length === 0) {
          return of([]);
        }
        
        // Then get property counts for each landlord
        const requests = landlords.map(landlord => 
          this.propertyService.getByLandlordId(landlord.id!).pipe(
            catchError(() => of([])) // Handle errors for individual landlords
          )
        );
        
        return forkJoin(requests);
      }),
      catchError(error => {
        this.snackBar.open('Error loading landlords', 'Close', { duration: 3000 });
        console.error(error);
        return of([]);
      })
    ).subscribe(propertiesArrays => {
      // Update property counts map
      this.propertyCounts.clear();
      
      if (propertiesArrays.length > 0) {
        this.allLandlords.forEach((landlord, index) => {
          if (landlord.id) {
            this.propertyCounts.set(landlord.id, propertiesArrays[index].length);
          }
        });
      }
      
      this.updateDisplayedLandlords();
      this.isLoading = false;
    });
  }
  
  getPropertyCount(landlordId: number): number {
    return this.propertyCounts.get(landlordId) || 0;
  }
  
  updateDisplayedLandlords() {
    const startIndex = this.currentPage * this.pageSize;
    this.displayedLandlords = this.allLandlords.slice(startIndex, startIndex + this.pageSize);
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updateDisplayedLandlords();
    this.selection.clear();
  }
  
  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.displayedLandlords.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.displayedLandlords);
  }
  
  /** Opens edit form for selected landlord */
  editSelected() {
    if (this.selection.selected.length !== 1) {
      this.snackBar.open('Please select exactly one landlord to edit', 'Close', { duration: 3000 });
      return;
    }
    
    const landlord = this.selection.selected[0];
    this.selectedLandlord = landlord;
    this.landlordForm.patchValue({
      name: landlord.name,
      email: landlord.email,
      phone: landlord.phone,
      identity: landlord.identity,
      pin: landlord.pin
    });
    
    // You could navigate to edit form or open a dialog here
    // For this example, we'll just scroll to the form
    document.getElementById('editForm')?.scrollIntoView({ behavior: 'smooth' });
  }
  
  /** Deletes selected landlords */
  deleteSelected() {
    if (this.selection.selected.length === 0) {
      this.snackBar.open('Please select at least one landlord to delete', 'Close', { duration: 3000 });
      return;
    }
    
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Delete',
        message: `Are you sure you want to delete ${this.selection.selected.length} landlord(s)?`
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Process deletions sequentially
        const deletePromises = this.selection.selected.map(landlord => 
          this.landlordService.delete(landlord.id!).toPromise()
        );
        
        Promise.all(deletePromises)
          .then(() => {
            this.snackBar.open(`${this.selection.selected.length} landlord(s) deleted successfully`, 'Close', { duration: 3000 });
            this.selection.clear();
            this.loadLandlords();
          })
          .catch(error => {
            this.snackBar.open('Error deleting landlords', 'Close', { duration: 3000 });
            console.error(error);
          });
      }
    });
  }

  submitForm() {
    if (this.landlordForm.invalid) {
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
      return;
    }

    const landlordData: Landlord = this.landlordForm.value;
    
    if (this.selectedLandlord) {
      // Update landlord
      this.landlordService.update(this.selectedLandlord.id!, landlordData).subscribe({
        next: () => {
          this.loadLandlords();
          this.snackBar.open('Landlord updated successfully', 'Close', { duration: 3000 });
          this.resetForm();
        },
        error: (err) => {
          this.snackBar.open('Error updating landlord', 'Close', { duration: 3000 });
          console.error(err);
        }
      });
    } else {
      // Create functionality is handled by the add-landlord component
      this.snackBar.open('Please use the Add Landlord form to create a new landlord', 'Close', { duration: 3000 });
    }
  }

  resetForm() {
    this.landlordForm.reset();
    this.selectedLandlord = null;
  }
  
  /** Clear all selections */
  clearSelection() {
    this.selection.clear();
  }
  
  /** Navigate to view properties for a specific landlord */
  viewProperties(landlord: Landlord) {
    this.router.navigate(['/properties'], { 
      queryParams: { 
        landlordId: landlord.id,
        landlordName: landlord.name 
      } 
    });
  }
  
  /** Navigate to add landlord page */
  goToAddLandlord() {
    this.router.navigate(['/landlords/add']);
  }
}
