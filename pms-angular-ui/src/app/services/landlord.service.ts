import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface Landlord {
  id?: number;
  name: string;
  email?: string;
  phone: string;
  identity?: string;
  pin?: string;
}

@Injectable({
  providedIn: 'root'
})

export class LandlordService {
  private apiUrl = `${environment.apiUrl}/landlords`;
  private token: string | null;

  constructor(private http: HttpClient) {
    this.token = localStorage.getItem('auth_token');
  }

  getHeaders(): HttpHeaders {
    if (!this.token) {
      console.error('No authentication token found');
    }

    return new HttpHeaders({
      'Authorization': `Bearer ${this.token}`,
      'Content-Type': 'application/json'
    });
  }

  getEndpoint(path: string): string {
    return `${this.apiUrl}/${path}`;
  }

  getAll(): Observable<Landlord[]> {  
    return this.http.get<Landlord[]>(this.getEndpoint('get-landlords'), { headers: this.getHeaders() });
  }

  getById(id: number): Observable<Landlord> {
    return this.http.get<Landlord>(`${this.getEndpoint('get-landlords')}/${id}`);
  }

  create(landlord: Landlord): Observable<Landlord> {
    return this.http.post<Landlord>(this.getEndpoint('create-landlord'), landlord, { headers: this.getHeaders() });
  }

  update(id: number, landlord: Landlord): Observable<Landlord> {
    return this.http.put<Landlord>(`${this.getEndpoint('update-landlord')}/${id}`, landlord);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.getEndpoint('delete-landlord')}/${id}`);
  }
}
