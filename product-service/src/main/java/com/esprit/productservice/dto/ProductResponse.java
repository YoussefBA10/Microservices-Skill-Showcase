package com.esprit.productservice.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProductResponse implements Serializable {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
