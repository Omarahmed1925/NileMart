package org.route.console;

import org.route.Cart.ShoppingCart;
import org.route.Service.ProductService;
import org.route.checkout.CheckoutService;
import org.route.common.StoreException;
import org.route.model.Customer;
import org.route.model.Governorate;
import org.route.model.Order;
import org.route.model.Product;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Scanner;

public class StoreConsole {

    private final ApplicationContext context;
    private final ProductService productService;
    private final CheckoutService checkoutService;
    private final Map<Integer, ShoppingCart> carts;

    private final Scanner scanner = new Scanner(System.in);
    private int nextCartId = 1;

    public StoreConsole(
            ApplicationContext context,
            ProductService productService,
            CheckoutService checkoutService,
            Map<Integer, ShoppingCart> carts
    ) {
        this.context = context;
        this.productService = productService;
        this.checkoutService = checkoutService;
        this.carts = carts;
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

            int choice = readInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> showProducts();
                    case 2 -> createCart();
                    case 3 -> addProduct();
                    case 4 -> showCart();
                    case 5 -> checkout();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (StoreException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showProducts() {
        System.out.println("\n========== PRODUCTS ==========");

        for (Product product : productService.findAll()) {
            System.out.println(product);
        }
    }

    private void createCart() {
        System.out.println("\n========== CREATE CART ==========");

        System.out.print("Customer name: ");
        String name = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();

        if (email.isBlank()) {
            email = null;
        }

        Customer customer = new Customer(name, phone, email);

        ShoppingCart cart = context.getBean(ShoppingCart.class);

        int cartId = nextCartId++;

        cart.open("CART-" + cartId, customer);

        carts.put(cartId, cart);

        System.out.println("Cart created successfully! ID: " + cartId);
    }

    private void addProduct() {
        System.out.println("\n========== ADD PRODUCT ==========");

        int cartId = readInt("Cart ID: ");
        ShoppingCart cart = getCart(cartId);

        System.out.print("Product SKU: ");
        String sku = scanner.nextLine();

        int quantity = readInt("Quantity: ");

        cart.addItem(sku, quantity);

        System.out.println("Product added successfully!");
    }

    private void showCart() {
        System.out.println("\n========== SHOW CART ==========");

        int cartId = readInt("Cart ID: ");
        ShoppingCart cart = getCart(cartId);

        System.out.println("Cart number: " + cart.getCartNumber());
        System.out.println("Customer: " + cart.getCustomer());

        System.out.println(cart.view());
    }

    private void checkout() {
        System.out.println("\n========== CHECKOUT ==========");

        int cartId = readInt("Cart ID: ");
        ShoppingCart cart = getCart(cartId);

        System.out.println("Available governorates:");

        for (Governorate governorate : Governorate.values()) {
            System.out.println("- " + governorate);
        }

        System.out.print("Governorate: ");
        String input = scanner.nextLine();

        Governorate governorate;

        try {
            governorate = Governorate.valueOf(
                    input.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid governorate.");
            return;
        }

        Order order = checkoutService.placeOrder(cart, governorate);

        System.out.println("Order placed successfully!");
        System.out.println("Order number: " + order.orderNumber());
        System.out.println("Subtotal: " + order.subTotal());
        System.out.println("VAT: " + order.vat());
        System.out.println("Shipping: " + order.shippingFee());
        System.out.println("Total: " + order.total());
    }

    private ShoppingCart getCart(int cartId) {
        ShoppingCart cart = carts.get(cartId);

        if (cart == null) {
            throw new StoreException("Cart not found: " + cartId);
        }

        return cart;
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}