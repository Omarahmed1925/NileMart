package org.route.Repository;

import jakarta.annotation.PreDestroy;
import org.route.common.StoreException;
import org.route.model.Category;
import org.route.model.Product;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;


@Repository
@Scope("singleton")
public class InMemoryProductRepository implements ProductRepository {
    // used concurrent hashmap to avoid concurrency issues in case of multiple threads accessing the repository
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("products.csv")))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String sku = parts[0].trim();
                    String name = parts[1].trim();
                    BigDecimal price = new BigDecimal(parts[3].trim());
                    Category category = Category.valueOf(parts[2].trim().toUpperCase());
                    int stockQuantity = Integer.parseInt(parts[4].trim());

                    Product product = new Product(sku, stockQuantity, name, price, category);
                    products.put(sku, product);
                }
            }
            System.out.println("Catalog loaded: " + products.size() + " products.");



        } catch (IOException cause) {
            throw new StoreException("The catalog could not be loaded.", cause);
        } catch (NullPointerException cause) {
            throw new StoreException("The catalog could not be loaded. The file 'products.csv' was not found.", cause);
        }

    }
    @PreDestroy
    public void destroy() {
        System.out.println("Catalog closed.");
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
