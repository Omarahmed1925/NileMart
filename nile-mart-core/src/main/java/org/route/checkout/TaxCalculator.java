package org.route.checkout;


import org.route.common.Money;
import org.route.common.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TaxCalculator {
    private final BigDecimal VAT_RATE;

  public TaxCalculator(@Value("${store.vat-rate}") BigDecimal vatRate){
      this.VAT_RATE = vatRate;
  }
  public BigDecimal calculate(BigDecimal subtotal){
      return Money.scale(subtotal.multiply(VAT_RATE));
  }
}
