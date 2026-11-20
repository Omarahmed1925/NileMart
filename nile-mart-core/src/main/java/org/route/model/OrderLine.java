package org.route.model;

public record OrderLine(String sku, String name, int quantity, double linePrice) {
}
