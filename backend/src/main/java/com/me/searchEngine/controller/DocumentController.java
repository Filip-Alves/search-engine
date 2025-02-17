package com.me.searchEngine.controller;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.me.searchEngine.model.DocumentEntity;
import com.me.searchEngine.service.DocumentIndexingService;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentIndexingService documentIndexingService;
    private final Faker faker = new Faker(); // Générateur de fausses données

    @PostMapping("/add")
    public ResponseEntity<DocumentEntity> addDocument(@RequestBody DocumentEntity document) {
        DocumentEntity savedDoc = documentIndexingService.addDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDoc);
    }
    
    @GetMapping("/test-serialization")
    public ResponseEntity<String> testSerialization() throws Exception {
        String json = documentIndexingService.testSerialization();
        return ResponseEntity.ok(json); 
    }

    
    @PostMapping("/add-bulk")
    public ResponseEntity<String> addDocumentsBulk(@RequestBody List<DocumentEntity> documents) {
        // Assigner l'ID et la date pour chaque document
        documents.forEach(doc -> {
            doc.setId(UUID.randomUUID().toString()); // Génère un ID unique pour chaque document
            doc.setCreatedAt(Instant.now()); // Assigner la date de création
        });

        // Appeler le service pour ajouter les documents en bulk
        try {
			documentIndexingService.addDocumentsBulk(documents);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("le add bulk encore");
			e.printStackTrace();
		}

        return ResponseEntity.status(HttpStatus.CREATED).body("Documents ajoutés avec succès");
    }
    
    @PostMapping("/add-bulk-fake")
    public String addBulkFakeDocuments(@RequestParam(defaultValue = "10") int count) {
        List<DocumentEntity> fakeDocuments = IntStream.range(0, count)
                .mapToObj(i -> DocumentEntity.builder()
                        .id(null) // L'ID sera généré dans `addDocumentsBulk`
                        .title(faker.book().title()) // Titre aléatoire
                        .content(faker.artist().name() + " se trouve à " + faker.address().cityName() + " entrain de mangé un(e) " + faker.food().fruit())
                        .createdAt(null) // La date sera générée dans `addDocumentsBulk`
                        .build())
                .collect(Collectors.toList());
        try {
            documentIndexingService.addDocumentsBulk(fakeDocuments);
            return "Indexation réussie de " + count + " documents fictifs !";
        } catch (Exception e) {
            return "Erreur lors de l'indexation : " + e.getMessage();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DocumentEntity> updateDocument(@PathVariable String id, @RequestBody DocumentEntity updatedDocument) {
        DocumentEntity updated = documentIndexingService.updateDocument(id, updatedDocument);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentIndexingService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
