package com.esprit.productservice.service;

import com.esprit.productservice.dto.ProductES;
import com.esprit.productservice.dto.ProductRequest;
import com.esprit.productservice.dto.ProductResponse;
import com.esprit.productservice.model.Product;
import com.esprit.productservice.repository.ProductESRepository;

import com.esprit.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductESRepository productElasticRepository;
    @Autowired
    private CacheManager cacheManager;
    @CacheEvict(value = "productsCache", allEntries = true)
    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
    }

    @Cacheable(value = "productsCache",key = "#root.methodName")
    public List<ProductResponse> getAllProducts() {
        List<ProductResponse> productResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(3,10);
        Iterable<ProductES> productsES = productElasticRepository.findAll(pageable);
        Cache cache = cacheManager.getCache("productsCache");
        System.out.println("Cache not found: " + cache);
        //productResponses.addAll(products.stream().map(this::mapToProductResponse).toList());
        productResponses.addAll(StreamSupport.stream(productsES.spliterator(),false)
                .map(this::mapToProductResponse)
                .toList());
        return productResponses;
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
    private ProductResponse mapToProductResponse(ProductES product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

}
