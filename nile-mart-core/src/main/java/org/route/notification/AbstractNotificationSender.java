package org.route.notification;

import org.route.model.Customer;

public abstract class AbstractNotificationSender implements NotificationSender{
    @Override
    public void sendNotification(Customer customer, String message) {
        if (supports(customer)) {
            String channel = channel();
            String address = address(customer);
            System.out.println("[" + channel + "] to " + address + ": " + message);
        } else {
            System.out.println("Notification not supported for customer: " + customer.fullName());

        }
    }

    protected abstract String channel();
    protected abstract String address(Customer customer);
}
