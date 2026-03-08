import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLandlordComponent } from './add-landlord.component';

describe('AddLandlordComponent', () => {
  let component: AddLandlordComponent;
  let fixture: ComponentFixture<AddLandlordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddLandlordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddLandlordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
