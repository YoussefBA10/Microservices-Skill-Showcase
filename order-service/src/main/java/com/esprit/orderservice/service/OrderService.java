package com.esprit.orderservice.service;

import org.springframework.security.core.Authentication;
import com.esprit.orderservice.dto.InventoryResponse;
import com.esprit.orderservice.dto.OrderLineItemsDto;
import com.esprit.orderservice.dto.OrderRequest;
import com.esprit.orderservice.event.OrderPlacedEvent;
import com.esprit.orderservice.model.Order;
import com.esprit.orderservice.model.OrderLineItems;
import com.esprit.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;
    private final ApplicationEventPublisher applicationEventPublisher;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        order.setPrice(orderLineItems.stream()
                .map(OrderLineItems::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Call Inventory Service, and place order if product is in
        // stock
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://localhost:8083/api/v1/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            Arrays.stream(inventoryResponseArray).forEach(System.out::println);
            boolean allProductsInStock = false;
            if (inventoryResponseArray.length>0) {
                 allProductsInStock = Arrays.stream(inventoryResponseArray)
                        .allMatch(inventoryResponse -> orderLineItems.stream()
                                .allMatch(orderLine -> orderLine.getSkuCode().equals(inventoryResponse.getSkuCode())
                                        && inventoryResponse.isInStock()));

                System.out.println("allProductsInStock: " + allProductsInStock);
            }

            if (allProductsInStock) {
                String token = webClientBuilder.build().get()
                        .uri("http://localhost:8084/api/v1/auth/requestToken")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                //get the logged user after validate the order
                System.out.println(token);
                orderRepository.save(order);
                // publish Order Placed Event
                applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
                return "Order Placed";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        });

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
