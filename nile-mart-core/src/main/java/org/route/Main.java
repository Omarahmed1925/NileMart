package org.route;

import org.route.Service.ProductService;
import org.route.config.AppConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            ProductService service = context.getBean(ProductService.class);

            // Price of 2 chargers
            System.out.println(service.getPrice("ACC-CHG-20W", 2));

            // Categories
            System.out.println(service.groupByCategory().keySet());

            // Out-of-stock products
            System.out.println(
                    service.search(p -> p.getStockQuantity() == 0)
            );

            // Spring bean names
            System.out.println(Arrays.toString(context.getBeanDefinitionNames()));
        }
    }
}