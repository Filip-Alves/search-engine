package com.me.searchEngine.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.me.searchEngine.model.DocumentEntity;

@Repository
public interface DocumentRepository extends ElasticsearchRepository<DocumentEntity, String> { 
	
	List<DocumentEntity> findByTitleContainingOrContentContaining(String title, String content);
	List<DocumentEntity> findAll();
	List<DocumentEntity> findAllByOrderByCreatedAtDesc();
}
