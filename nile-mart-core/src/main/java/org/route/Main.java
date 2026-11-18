package org.route;

import org.route.config.AppConfig;
import org.route.model.Customer;
import org.route.model.Governorate;
import org.route.notification.NotificationSender;
import org.route.shipping.PushNotificationSender;
import org.route.shipping.ShippingQuoteService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            ShippingQuoteService shippingService =
                    context.getBean(ShippingQuoteService.class);

            // TEST 1: Cairo -> Free shipping
            BigDecimal cairoShipping = shippingService.quote(
                    Governorate.CAIRO,
                    new BigDecimal("5000.00")
            );

            System.out.println("Cairo shipping: " + cairoShipping);

            // TEST 2: Matrouh -> Normal shipping
            BigDecimal matrouhShipping = shippingService.quote(
                    Governorate.MATROUH,
                    new BigDecimal("999999.00")
            );

            System.out.println("Matrouh shipping: " + matrouhShipping);

            // TEST 3: Customer with email
            Customer customer1 = new Customer(
                    "Ahmed",
                    "01012345678",
                    "ahmed@gmail.com"
            );

            System.out.println("\nCustomer with email:");

            for (NotificationSender sender :
                    context.getBeansOfType(NotificationSender.class).values()) {

                sender.sendNotification(customer1, "Order confirmed!");
            }

            // TEST 4: Customer without email
            Customer customer2 = new Customer(
                    "Ali",
                    "01112345678",
                    null
            );

            System.out.println("\nCustomer without email:");

            for (NotificationSender sender :
                    context.getBeansOfType(NotificationSender.class).values()) {

                sender.sendNotification(customer2, "Order confirmed!");
            }

            PushNotificationSender push =
                    context.getBean(PushNotificationSender.class);

            push.sendNotification(customer1, "Order confirmed!");
        }
    }
}