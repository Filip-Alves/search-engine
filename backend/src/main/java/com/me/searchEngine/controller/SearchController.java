package com.me.searchEngine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.me.searchEngine.model.DocumentEntity;
import com.me.searchEngine.service.DocumentSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final DocumentSearchService documentSearchService;

    @GetMapping
    public List<DocumentEntity> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return documentSearchService.search(query, sortBy, order, limit);
    }

    @GetMapping("/all")
    public List<DocumentEntity> getAllDocuments(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return documentSearchService.getAllDocuments(sortBy, order, limit);
    }
}