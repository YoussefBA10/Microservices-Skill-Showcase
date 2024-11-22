package com.esprit.productservice.service;

import com.esprit.productservice.dto.ProductRequest;
import com.esprit.productservice.dto.ProductResponse;
import com.esprit.productservice.model.Product;
import com.esprit.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    private CacheManager cacheManager;
    @CacheEvict(value = "productsCache",key = "#root.methodName")
    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    @Cacheable(value = "productsCache",key = "#root.methodName")
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        Cache cache = cacheManager.getCache("productsCache");
            System.out.println("Cache not found: " + cache);

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
