package org.route;

import org.route.Cart.ShoppingCart;
import org.route.Service.ProductService;
import org.route.checkout.CheckoutService;
import org.route.config.AppConfig;
import org.route.console.StoreConsole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            Map<Integer, ShoppingCart> carts = new HashMap<>();

            StoreConsole console = new StoreConsole(
                    context,
                    context.getBean(ProductService.class),
                    context.getBean(CheckoutService.class),
                    carts
            );

            console.run();
        }
    }
}