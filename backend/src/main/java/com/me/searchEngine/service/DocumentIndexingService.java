package com.me.searchEngine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.me.searchEngine.exception.ResourceNotFoundException;
import com.me.searchEngine.model.DocumentEntity;
import com.me.searchEngine.repository.DocumentRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIndexingService {
    private final DocumentRepository documentRepository;
    private final ElasticsearchClient elasticsearchClient;
    

    
    private final ObjectMapper objectMapper;

    public String testSerialization() throws Exception {
        DocumentEntity doc = new DocumentEntity("1", "Test", "Contenu", Instant.now());
        return objectMapper.writeValueAsString(doc);
    }


    public DocumentEntity addDocument(DocumentEntity document) {
        document.setCreatedAt(Instant.now()); // Assigne automatiquement la date
        return documentRepository.save(document);
    }
    
    public void addDocumentsBulk(List<DocumentEntity> documents) throws IOException {
        try {
            
        	documents.forEach(doc -> {
        	    if (doc.getId() == null || doc.getId().isEmpty()) {
        	        doc.setId(UUID.randomUUID().toString()); 
        	    }
        	    if (doc.getCreatedAt() == null) {
        	        doc.setCreatedAt(Instant.now()); 
        	    }
        	});

            // Construction de la requête Bulk
            BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
            List<BulkOperation> operations = documents.stream()
                .map(doc -> new BulkOperation.Builder()
                    .index(new IndexOperation.Builder<DocumentEntity>()
                        .index("documents")
                        .document(doc)
                        .build())
                    .build())
                .collect(Collectors.toList());

            bulkRequest.operations(operations);
            log.info("Envoi d'une requête bulk à Elasticsearch avec {} documents", documents.size());

            elasticsearchClient.bulk(bulkRequest.build());

            log.info("Indexation en masse réussie !");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'indexation en masse : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur d'indexation Elasticsearch", e);
        }
    }
    
    @PostConstruct
    public void configureObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule()); 
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public DocumentEntity updateDocument(String id, DocumentEntity updatedDocument) {
        return documentRepository.findById(id)
                .map(existingDocument -> {
                    existingDocument.setTitle(updatedDocument.getTitle());
                    existingDocument.setContent(updatedDocument.getContent());
                    existingDocument.setCreatedAt(updatedDocument.getCreatedAt());
                    return documentRepository.save(existingDocument);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé avec l'ID : " + id)); 
    }

    public void deleteDocument(String id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document non trouvé avec l'ID : " + id);
        }
        documentRepository.deleteById(id);
    }
}