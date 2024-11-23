package com.esprit.productservice.repository;

import com.esprit.productservice.dto.ProductES;
import com.esprit.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


public interface ProductESRepository extends ElasticsearchRepository<ProductES, Integer> {
}

