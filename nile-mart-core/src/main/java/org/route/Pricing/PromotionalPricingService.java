package org.route.Pricing;

import org.springframework.beans.factory.annotation.Value;
import org.route.common.Money;
import org.route.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PromotionalPricingService implements PricingService {
    private final BigDecimal discountPercentage;

    public PromotionalPricingService(@Value("${store.discount-percentage}") BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal getPrice(Product product, int quantity) {
        return Money.scale(
                product.getProductPrice()
                        .multiply(BigDecimal.valueOf(quantity))
                        .multiply(BigDecimal.ONE.subtract(discountPercentage))
        );
    }


}