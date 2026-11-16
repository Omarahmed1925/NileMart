package org.route.Pricing;

import org.route.common.Money;
import org.route.model.Product;

import java.math.BigDecimal;

public class PromotionalPricingService implements PricingService {

    private final BigDecimal discountPercentage = new BigDecimal("0.20");

    @Override
    public BigDecimal getPrice(Product product, int quantity) {
        return Money.scale(
                product.getProductPrice()
                        .multiply(BigDecimal.valueOf(quantity))
                        .multiply(BigDecimal.ONE.subtract(discountPercentage))
        );
    }


}