package org.route.model;

import org.route.common.Money;

import java.math.BigDecimal;
import java.util.List;

public record Order(String orderNumber, Customer customer, Governorate governorate, List<OrderLine> orderLines, BigDecimal subTotal
        , BigDecimal vat, BigDecimal shippingFee, BigDecimal total) {

    public Order{
        orderLines = List.copyOf(orderLines);
        subTotal = Money.scale(subTotal);
        vat = Money.scale(vat);
        shippingFee = Money.scale(shippingFee);
        total = Money.scale(total);
    }

}
