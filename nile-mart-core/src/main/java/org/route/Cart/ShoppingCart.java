package org.route.Cart;

import lombok.Getter;
import org.route.common.Money;
import org.route.common.OutOfStockException;
import org.route.common.StoreException;
import org.route.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.route.Service.ProductService;
import org.route.model.Customer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class ShoppingCart {
    private final ProductService productService;
    private final int MAX_ITEMS;
    @Getter
    private String cartNumber;
    @Getter
    private Customer customer;
    private LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();

    public ShoppingCart(ProductService productService, @Value("${store.max-quantity-per-product}") int maxItems) {
        this.productService = productService;
        this.MAX_ITEMS = maxItems;
    }

    public void open(String cartNumber, Customer customer) {

        if (this.cartNumber != null) {
            throw new IllegalStateException("org.route.Cart is already opened");
        }

        this.cartNumber = cartNumber;
        this.customer = customer;
    }

    public synchronized void addItem(String sku, int quantity) {

        if (quantity < 1) {
            throw new StoreException("Invalid quantity: " + quantity);
        }
        Product product = productService.findBySku(sku);

        if (product.getStockQuantity() <= 0) {
            throw new OutOfStockException(product.getName() + " is out of stock ");
        }
        int newQuantity = lines.getOrDefault(sku, 0) + quantity;
        if (newQuantity > MAX_ITEMS) {
            throw new StoreException("Cannot add more than " + MAX_ITEMS + " items of product: " + product.getName());
        }
        if (newQuantity > product.getStockQuantity()) {
            throw new OutOfStockException("Not enough stock for product: " + product.getName());
        }
        lines.put(sku, newQuantity);

    }

    public BigDecimal subtotal() {
        return lines.entrySet().
                stream()
                .map(entry -> productService.getPrice(entry.getKey(), entry.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
    }

    public Map<String, Integer> getLines() {
        return Collections.unmodifiableMap(lines);
    }

    public CartView view() {
        List<LineView> linesList = lines.entrySet().stream()
                .map(entry -> new LineView(entry.getKey(), productService.findBySku(entry.getKey()).getName(), entry.getValue(), productService.getPrice(entry.getKey(), entry.getValue())))
                .toList();
        int itemCount = linesList.stream().mapToInt(LineView::quantity).sum();
        BigDecimal subTotal = linesList.stream()
                .map(LineView::linePrice)
                .reduce(Money.ZERO, BigDecimal::add);
        return new CartView(cartNumber, customer, linesList, itemCount, subTotal, isEmpty());
    }

}
