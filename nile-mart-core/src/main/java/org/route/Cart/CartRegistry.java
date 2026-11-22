package org.route.Cart;

import org.route.common.Check;
import org.route.common.NotFoundException;
import org.route.model.Customer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class CartRegistry {
    private final ObjectProvider<ShoppingCart> shoppingCartProvider;
    private final AtomicInteger cartCounter = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, ShoppingCart> registry = new ConcurrentHashMap<>();

    public CartRegistry(ObjectProvider<ShoppingCart> shoppingCartProvider) {
        this.shoppingCartProvider = shoppingCartProvider;
    }

    public ShoppingCart open(Customer customer) {
       ShoppingCart shoppingCart = shoppingCartProvider.getObject();
        int cartNumber = cartCounter.incrementAndGet();
        shoppingCart.open(cartNumber, customer);
        registry.put(cartNumber, shoppingCart);
        return shoppingCart;
    }

    public ShoppingCart get(int cartNumber) {
        return Check.found(Optional.ofNullable(registry.get(cartNumber)), () -> new NotFoundException("Cart " + cartNumber + " was not found.")
        );
    }

    public List<CartView> views() {
        return registry.values().stream()
                .map(ShoppingCart::view)
                .sorted(Comparator.comparingInt(CartView::number))
                .toList();
    }




}
