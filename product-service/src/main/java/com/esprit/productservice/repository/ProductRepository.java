package com.esprit.productservice.repository;

import com.esprit.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


public interface ProductRepository extends MongoRepository<Product, Integer> {
}
