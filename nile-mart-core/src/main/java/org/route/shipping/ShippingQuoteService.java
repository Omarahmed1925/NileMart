package org.route.shipping;


import org.route.model.Governorate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingQuoteService {
    private final ShippingCalculator normalShippingCalculator;
    private final ShippingCalculator freeShippingCalculator;
    private final BigDecimal threshold;

    public ShippingQuoteService(ShippingCalculator normalShippingCalculator,
                                @Qualifier("freeShipping")
                                ShippingCalculator freeShippingCalculator,
                                @Value("${store.free-shipping-threshold}")
                                BigDecimal threshold

    ) {
        this.normalShippingCalculator = normalShippingCalculator;
        this.freeShippingCalculator = freeShippingCalculator;
        this.threshold = threshold;
    }

    public BigDecimal quote(Governorate governorate, BigDecimal subtotal) {

        if (subtotal.compareTo(threshold) >= 0
                && governorate.getZone().isFreeShippingEligible()) {

            return freeShippingCalculator.calculate(governorate);
        }

        return normalShippingCalculator.calculate(governorate);
    }

}
