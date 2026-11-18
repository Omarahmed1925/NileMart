package org.route.shipping;

import org.route.model.Governorate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("freeShipping")
public class FreeShippingCalculator implements ShippingCalculator {

    @Override
    public BigDecimal calculate(Governorate governorate) {
        return new BigDecimal("0.00");
    }
}
