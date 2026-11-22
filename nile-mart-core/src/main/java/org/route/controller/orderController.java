package org.route.controller;

import lombok.RequiredArgsConstructor;
import org.route.checkout.OrderRegistry;
import org.route.common.Check;
import org.route.common.NotFoundException;
import org.route.model.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRegistry orderRegistry;

    @GetMapping("/orders")
    public String list(Model model) {
        model.addAttribute("orders", orderRegistry.findAll());
        model.addAttribute("revenue", orderRegistry.revenue());
        return "orders/list";
    }

    @GetMapping("/orders/{orderNumber}")
    public String view(@PathVariable("orderNumber") String orderNumber, Model model) {
        Order order = Check.found(
                orderRegistry.findByNumber(orderNumber),
                () -> new NotFoundException("Order " + orderNumber + " was not found.")
        );
        model.addAttribute("order", order);

        return "orders/confirmation";
    }

}
