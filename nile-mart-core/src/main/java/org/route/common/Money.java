package org.route.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class Money {

    private Money() {}

    public static BigDecimal scale(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
    public static String format(BigDecimal money) {
        DecimalFormat formatter =
                (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);

        formatter.applyPattern("#,##0.00");

        return formatter.format(money);
    }
}