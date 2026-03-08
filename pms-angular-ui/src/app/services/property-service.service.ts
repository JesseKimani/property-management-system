import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment'

export interface Property {
  id?: number;
  name: string;
  location: string;
  landlordId: number;
  
}

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/properties`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Property[]> {
    return this.http.get<Property[]>(this.apiUrl);
  }

  getById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.apiUrl}/${id}`);
  }

  getByLandlordId(landlordId: number): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.apiUrl}/get-properties-by-landlord/${landlordId}`);
  }

  create(property: Property): Observable<Property> {
    return this.http.post<Property>(this.apiUrl, property);
  }

  update(id: number, property: Property): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, property);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
