package com.esprit.inventoryservice.service;

import com.esprit.inventoryservice.dto.InventoryResponse;
import com.esprit.inventoryservice.repository.InventoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "inventoryFallback")
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Checking Inventory");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }
    private List<InventoryResponse> inventoryFallback() {
        log.info("Inventory Service failed! ");
        return List.of(new InventoryResponse("Unavailable",false));
    }
}
