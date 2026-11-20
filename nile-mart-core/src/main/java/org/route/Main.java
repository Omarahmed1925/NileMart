package org.route;

import org.route.Cart.ShoppingCart; // Adjust package if needed
import org.route.Service.ProductService;
import org.route.config.AppConfig;
import org.route.model.Customer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            // 1. Test singleton vs prototype
            ProductService service1 = context.getBean(ProductService.class);
            ProductService service2 = context.getBean(ProductService.class);

            ShoppingCart cart1 = context.getBean(ShoppingCart.class);
            ShoppingCart cart2 = context.getBean(ShoppingCart.class);

            System.out.println("ProductService singleton: " + (service1 == service2));
            System.out.println("ShoppingCart prototype: " + (cart1 == cart2));

            cart1.open("CART-001", new Customer(
                    "Ahmed", "01012345678", "ahmed@gmail.com"));

            cart2.open("CART-002", new Customer(
                    "Ali", "01112345678", null));
            // 3. Add products
            cart1.addItem("ACC-CHG-20W", 2);
            cart1.addItem("FSH-GAL-001", 1);

            cart2.addItem("ACC-BUDS-6", 3);

            // 4. Test snapshots
            System.out.println("\nCart 1:");
            System.out.println(cart1.view());

            System.out.println("\nCart 2:");
            System.out.println(cart2.view());

            // 5. Test merged quantities
            cart1.addItem("ACC-CHG-20W", 1);

            System.out.println("\nCart 1 after adding another charger:");
            System.out.println(cart1.view());

            // 6. Test clear()
            cart2.clear();

            System.out.println("\nCart 2 empty after clear: " + cart2.isEmpty());
        }

        // @PreDestroy should print "Catalog closed." here
    }
}