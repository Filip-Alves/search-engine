import { Component, OnInit } from '@angular/core';
import { DocumentSearchService } from '../../services/document-search.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentListComponent } from '../document-list/document-list.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, DocumentListComponent],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  query: string = '';
  sortBy: string = 'createdAt';
  limit: number = 10;
  results: any[] = [];
  isDesc: boolean = false;

  constructor(private documentSearchService: DocumentSearchService, private http: HttpClient) {}

  ngOnInit(): void {
    this.search();  
  }

  search() {
    const trimmedQuery = this.query.trim();
    const apiUrl = trimmedQuery ? '/api/search' : '/api/search/all';

    
    const order = this.isDesc ? 'desc' : 'asc';

    
    const params: any = { sortBy: this.sortBy, limit: this.limit, order: order };

    if (trimmedQuery) {
      params.query = trimmedQuery;
    }

    
    this.documentSearchService.searchDocuments(
      params.query || '',        
      params.sortBy,             
      params.limit,
      params.order
    ).subscribe({
      next: (data) => this.results = data,
      error: (error) => console.error('Erreur lors de la recherche :', error)
    });
  }

  addBulkFakeDocuments() {
    const url = 'http://localhost:8080/api/documents/add-bulk-fake?count=2';
  
    this.http.post(url, {}, { responseType: 'text' }).subscribe({
      next: (response) => {
        console.log('Réponse du serveur :', response);
        console.log('Faux documents ajoutés');
        this.search();
      },
      error: (error) => {
        console.error('Erreur lors de l\'ajout des documents :', error);
      }
    });
  }
  
}
