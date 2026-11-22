package org.route.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Product {
    @EqualsAndHashCode.Include
    private String sku;
    @Setter
    private int stockQuantity;
    private String name;
    private BigDecimal price;
    private Category category;


}
