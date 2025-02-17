import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface Document {
  id: string;
  title: string;
  content: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentSearchService {
  private apiUrl = 'http://localhost:8080';
  private http = inject(HttpClient);

  searchDocuments(query: string, sortBy: string, limit: number, order: string): Observable<Document[]> {
    let params = new HttpParams();
    
    
    if (query.trim()) {
        params = params.set('query', query);
    }
    if (sortBy.trim()) {
        params = params.set('sortBy', sortBy);
    }
    if (limit > 0) {
        params = params.set('limit', limit.toString());
    }
    if (order === 'desc') {
        params = params.set('order', 'desc');
    } else {
      params = params.set('order', 'asc');
    }

    // Si `query` est vide, appeler `/api/search/all`
    const apiUrl = query.trim() ? '/api/search' : '/api/search/all';

    return this.http.get<Document[]>(`${this.apiUrl}${apiUrl}`, { params });
  }

}
