package org.route.Service;

import lombok.RequiredArgsConstructor;
import org.route.Pricing.PricingService;
import org.route.Repository.ProductRepository;
import org.route.common.Check;
import org.route.common.NotFoundException;
import org.route.common.OutOfStockException;
import org.route.model.Category;
import org.route.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final PricingService pricingService;


    public List<Product> findAll() {
        return productRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getCategory)
                        .thenComparing(Product::getName))
                .toList();
    }

    public Product findBySku(String sku){
        return Check.found(
                productRepository.findBySku(sku),
                () -> new NotFoundException("Product " + sku + " was not found.")
        );
    };

    public BigDecimal getPrice(String sku, int qty){
        Product product = findBySku(sku);
        return pricingService.getPrice(product, qty);
    };

    public void reduceStock(String sku, int qty) {

        Product product = findBySku(sku);

        int currentStock = product.getStockQuantity();

        if (currentStock < qty) {
            throw new OutOfStockException(
                    "Only " + currentStock + " available for " + product.getName() + "."
            );
        }
        product.setStockQuantity(currentStock - qty);
    }
    public List<Product> search(Predicate<Product> filter){
        return productRepository.findAll()
                .stream()
                .filter(filter)
                .toList();
    }
    public Map<Category, List<Product>> groupByCategory(){
        return productRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory, () -> new EnumMap<>(Category.class), Collectors.toList()));
    }

    public BigDecimal getProductPrice(String sku, int qty){
        Product product = findBySku(sku);
        return pricingService.getPrice(product, qty);
    }

}
