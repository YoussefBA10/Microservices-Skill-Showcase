package com.esprit.productservice.event;

import com.esprit.productservice.dto.ProductES;
import com.esprit.productservice.model.Product;
import com.esprit.productservice.repository.ProductESRepository;
import com.esprit.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;

import static java.lang.Integer.parseInt;

@Component
@AllArgsConstructor
@EnableAsync
public class MongoEventListener {
    private final ProductESRepository productESRepository;
    private final ProductRepository productRepository;
    @EventListener
    @Async
    public void handleAfterSave(AfterSaveEvent<Product> event) {
        Product product = event.getSource();
        ProductES productES = new ProductES(product.getId(), product.getName(), product.getDescription(), product.getPrice());
        System.out.println(productES);
        productESRepository.save(productES);
    }

    @EventListener
    @Async
    public void handleAfterDelete(AfterDeleteEvent<Product> event) {
        try {
            Integer productId = parseInt(event.getSource().get("id").toString());
            productESRepository.deleteById(productId);
        }catch (NumberFormatException err){
            System.out.println(err);
        }
    }
    @Scheduled(fixedRate = 30000)
    @Async
    public void syncProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ProductES productES = new ProductES(product.getId(), product.getName(), product.getDescription(), product.getPrice());
            productESRepository.save(productES);
        }
    }
}
