package org.route;

import org.route.Pricing.PricingService;
import org.route.Pricing.PromotionalPricingService;
import org.route.Pricing.StandardPricingService;
import org.route.Repository.InMemoryProductRepository;
import org.route.Repository.ProductRepository;
import org.route.Service.ProductService;
import org.route.model.Product;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        InMemoryProductRepository productRepository = new InMemoryProductRepository();
        productRepository.init();
        PricingService pricingService = new StandardPricingService();
        PricingService promotionalPricingService = new PromotionalPricingService();
        ProductService productService = new ProductService(productRepository, pricingService);
        ProductService promotionalProductService = new ProductService(productRepository, promotionalPricingService);

        // pricing prints
        System.out.println(productService.getPrice("ACC-CHG-20W", 2));
        System.out.println(promotionalProductService.getPrice("ACC-CHG-20W", 2));

        System.out.println("\n");
        // groupByCategory key prints
        System.out.println(productService.groupByCategory().keySet());
        System.out.println("\n");

        // out of stock search result
        System.out.println(productService.search(product -> product.getStockQuantity() == 0));

        System.out.println("\n");
        // product equality test
        Product p1 = productService.findBySku("ACC-CHG-20W");
        Product p2 = productService.findBySku("ACC-CHG-20W");
        Set<Product> products = new HashSet<>();
        products.add(p1);
        products.add(p2);
        System.out.println(products.size());
        System.out.println(p1.equals(p2));

        System.out.println("\n");

    }
}
