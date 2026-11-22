package org.route.console;


import org.route.Cart.CartRegistry;
import org.route.Cart.ShoppingCart;
import org.route.checkout.CheckoutService;
import org.route.common.StoreException;
import org.route.model.Customer;
import org.route.model.Governorate;
import org.route.model.Order;
import org.route.Service.ProductService;

import java.util.Scanner;

public class StoreConsole {

    private final CartRegistry cartRegistry;
    private final ProductService productService;
    private final CheckoutService checkoutService;
    private final Scanner scanner = new Scanner(System.in);

    public StoreConsole(
            CartRegistry cartRegistry,
            ProductService productService,
            CheckoutService checkoutService) {

        this.cartRegistry = cartRegistry;
        this.productService = productService;
        this.checkoutService = checkoutService;
    }

    public void run() {

        while (true) {
            System.out.println("""
                    
                    ========== NILEMART ==========
                    1. Show products
                    2. Create a cart
                    3. Add a product to a cart
                    4. Show a cart
                    5. Check out a cart
                    0. Exit
                    """);

            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                return;
            }

            try {
                switch (choice) {
                    case "1" -> showProducts();
                    case "2" -> createCart();
                    case "3" -> addProduct();
                    case "4" -> showCart();
                    case "5" -> checkout();
                    default -> System.out.println("Invalid option.");
                }
            } catch (StoreException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showProducts() {
        productService.findAll().forEach(System.out::println);
    }

    private void createCart() {

        System.out.print("Full name: ");
        String name = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();

        Customer customer = new Customer(name, phone, email);

        ShoppingCart cart = cartRegistry.open(customer);

        System.out.println("Cart created: " + cart.view().number());
    }

    private void addProduct() {

        System.out.print("Cart number: ");
        int number = Integer.parseInt(scanner.nextLine());

        ShoppingCart cart = cartRegistry.get(number);

        System.out.print("Product SKU: ");
        String sku = scanner.nextLine();

        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        cart.addItem(sku, quantity);

        System.out.println("Product added successfully.");
    }

    private void showCart() {

        System.out.print("Cart number: ");
        int number = Integer.parseInt(scanner.nextLine());

        ShoppingCart cart = cartRegistry.get(number);

        System.out.println(cart.view());
    }

    private void checkout() {

        System.out.print("Cart number: ");
        int number = Integer.parseInt(scanner.nextLine());

        ShoppingCart cart = cartRegistry.get(number);

        System.out.print("Governorate (e.g. GIZA): ");
        Governorate governorate =
                Governorate.valueOf(scanner.nextLine().trim().toUpperCase());

        System.out.println("Quote: " +
                checkoutService.quote(cart, governorate));

        System.out.print("Place order? (y/n): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("y")) {
            return;
        }

        Order order = checkoutService.placeOrder(cart, governorate);

        System.out.println("Order confirmed: " + order.orderNumber());
    }
}