package org.route.Pricing;

import org.route.common.Money;
import org.route.model.Product;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Primary
@Component
public class StandardPricingService implements PricingService {
    @Override
    public BigDecimal getPrice(Product product, int quantity) {
        return Money.scale(
                product.getProductPrice().multiply(BigDecimal.valueOf(quantity))
        );
    }
}
