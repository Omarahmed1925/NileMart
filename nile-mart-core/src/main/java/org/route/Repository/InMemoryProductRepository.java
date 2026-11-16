package org.route.Repository;

import org.route.model.Category;
import org.route.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

public class InMemoryProductRepository implements ProductRepository {
    // used concurrent hashmap to avoid concurrency issues in case of multiple threads accessing the repository
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public void init(){
        products.put("PH-X5-128", new Product("PH-X5-128", 12, "Smartphone X5 128GB", new BigDecimal("16000.00"), Category.ELECTRONICS));

        products.put("ACC-CHG-20W", new Product("ACC-CHG-20W", 40, "Fast Charger 20W", new BigDecimal("650.00"), Category.ELECTRONICS));

        products.put("ACC-BUDS-6", new Product("ACC-BUDS-6", 8, "Wireless Earbuds", new BigDecimal("1100.00"), Category.ELECTRONICS));

        products.put("FSH-GAL-001", new Product("FSH-GAL-001", 25, "Cotton Galabeya", new BigDecimal("900.00"), Category.FASHION));

        products.put("HOM-PAN-28", new Product("HOM-PAN-28", 3, "Non-stick Frying Pan 28cm", new BigDecimal("1200.00"), Category.HOME_KITCHEN));

        products.put("HOM-SPC-RCK", new Product("HOM-SPC-RCK", 10, "Wooden Spice Rack", new BigDecimal("750.00"), Category.HOME_KITCHEN));

        products.put("BTY-SUN-50", new Product("BTY-SUN-50", 0, "Sun Cream SPF50", new BigDecimal("350.00"), Category.BEAUTY));

        products.put("BK-CAI-TRI", new Product("BK-CAI-TRI", 15, "The Cairo Trilogy", new BigDecimal("450.00"), Category.BOOKS));

    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public Optional<Product> findById(String s) {
        return Optional.ofNullable(products.get(s));
    }

    @Override
    public Product save(Product entity) {
        products.put(entity.getSku(), entity);
        return entity;
    }
}
