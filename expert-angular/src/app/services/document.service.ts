import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Document } from '../entities/document';
import { Categorization } from '../entities/categorization';
import { Fullanalysis } from '../entities/fullanalysis';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private baseUrl = '/api/v1';

  constructor(private http: HttpClient) { }

  upload(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/documents/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }

  getFiles(): Observable<Document[]> {
    return this.http.get<Document[]>(`${this.baseUrl}/documents`);
  }

  getDocument(id: string): Observable<Document> {
    return this.http.get<Document>(`${this.baseUrl}/documents/${id}`);
  }

  getDocumentContent(id: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/documents/${id}/content`, {responseType: 'text'});
  }

  categorize(id: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/categorizations/document/${id}/process`);
  }

  getCategorizations(id: string): Observable<Categorization[]> {
    return this.http.get<Categorization[]>(`${this.baseUrl}/categorizations/document/${id}`);
  }

  analyze(id: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/fullanalysis/document/${id}/process`);
  }

  getFullAnalysis(id: string): Observable<Fullanalysis[]> {
    return this.http.get<Fullanalysis[]>(`${this.baseUrl}/fullanalysis/document/${id}`);
  }
}
