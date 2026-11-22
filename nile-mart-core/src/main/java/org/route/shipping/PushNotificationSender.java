package org.route.shipping;

import org.route.model.Customer;
import org.route.notification.AbstractNotificationSender;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class PushNotificationSender extends AbstractNotificationSender {

    @Override
    protected String channel() {
        return "PUSH";
    }

    @Override
    protected String address(Customer customer) {
        return customer.phone();
    }

}