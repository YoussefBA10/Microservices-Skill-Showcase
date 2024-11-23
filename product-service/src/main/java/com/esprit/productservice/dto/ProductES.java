package com.esprit.productservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

@Document(indexName = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductES {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
