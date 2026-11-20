package org.route.checkout;

import java.math.BigDecimal;

public record Quote(BigDecimal subtotal, BigDecimal vat, BigDecimal shippingCost, BigDecimal total) {
}
