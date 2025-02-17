package com.me.searchEngine.service;

import com.me.searchEngine.model.DocumentEntity;
import com.me.searchEngine.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentSearchService {
    private final DocumentRepository documentRepository;

    public List<DocumentEntity> search(String query, String sortBy, String order, int limit) {
        return documentRepository.findByTitleContainingOrContentContaining(query, query)
                .stream()
                .sorted(getComparator(sortBy, order))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DocumentEntity> getAllDocuments(String sortBy, String order, int limit) {
        return documentRepository.findAll()
                .stream()
                .sorted(getComparator(sortBy, order))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Comparator<DocumentEntity> getComparator(String sortBy, String order) {
        Comparator<DocumentEntity> comparator;
        switch (sortBy) {
            case "title":
                comparator = Comparator.comparing(
                        DocumentEntity::getTitle,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
                break;
            case "content":
                comparator = Comparator.comparing(
                        DocumentEntity::getContent,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
                break;
            case "createdAt":
            default:
                comparator = Comparator.comparing(
                        DocumentEntity::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
        }
        
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        
        return comparator.thenComparing(
                DocumentEntity::getTitle,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );
    }
}

