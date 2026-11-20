package org.route.checkout;

import lombok.RequiredArgsConstructor;
import org.route.Cart.ShoppingCart;
import org.route.Service.ProductService;
import org.route.common.Money;
import org.route.common.OutOfStockException;
import org.route.common.StoreException;
import org.route.model.Governorate;
import org.route.model.Order;
import org.route.model.OrderLine;
import org.route.model.Product;
import org.route.notification.NotificationSender;
import org.route.shipping.ShippingQuoteService;
import org.springframework.stereotype.Service;

import javax.swing.border.AbstractBorder;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public Quote quote(ShoppingCart cart, Governorate g) {
        if (cart == null || cart.isEmpty()) {
            throw new StoreException("Cart cannot be empty");
        }
        BigDecimal subTotal = cart.subtotal();
        BigDecimal vat = taxCalculator.calculate(subTotal);
        BigDecimal shippingCost = shippingQuoteService.quote(g, subTotal);
        BigDecimal total = subTotal.add(vat).add(shippingCost);
        return new Quote(subTotal, vat, shippingCost, total);
    }

    public synchronized Order placeOrder(ShoppingCart cart, Governorate g) {
        if (cart == null || cart.isEmpty()) {
            throw new StoreException("Cart cannot be empty");
        }
        cart.getLines().forEach((sku, quantity) -> {
            Product product = productService.findBySku(sku);

            if (product.getStockQuantity() < quantity) {
                throw new OutOfStockException("Not enough stock");
            }
        });
        Quote q = quote(cart, g);
        cart.getLines().forEach((sku, quantity) -> productService.reduceStock(sku, quantity));
        List orderLines = cart.getLines().entrySet().stream()
                .map(entry -> {
                    Product product = productService.findBySku(entry.getKey());
                    BigDecimal linePrice = product.getProductPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                    return new OrderLine(product.getSku(), product.getName(), entry.getValue(), linePrice.doubleValue());
                })
                .toList();
        Order order = new Order(orderRegistry.nextNumber(), cart.getCustomer(),g, orderLines, q.subtotal(), q.vat(), q.shippingCost(), q.total());
        orderRegistry.save(order);
        String message = "Order " + order.orderNumber()
                + " confirmed. Total " + Money.format(order.total());
        notificationSenders.forEach(sender -> sender.sendNotification(order.customer(),message));
        cart.clear();
        return order;
    }




}
