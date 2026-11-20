package org.route.Cart;

import org.route.model.Customer;

import java.math.BigDecimal;
import java.util.List;

public record CartView(String number, Customer customer, List<LineView> lines, int itemCount, BigDecimal subtotal, boolean empty){

}
