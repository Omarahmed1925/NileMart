package org.route.controller;

import lombok.RequiredArgsConstructor;
import org.route.Cart.CartRegistry;
import org.route.Cart.ShoppingCart;
import org.route.checkout.CheckoutService;
import org.route.common.StoreException;
import org.route.model.Governorate;
import org.route.model.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final CartRegistry cartRegistry;
    private final CheckoutService checkoutService;

    @GetMapping("/carts/{number}/checkout")
    public String form(@PathVariable("number") int number,
                       @RequestParam(name = "governorate", required = false) Governorate governorate,
                       Model model) {
        ShoppingCart cart = cartRegistry.get(number);
        model.addAttribute("cart", cart.view());
        model.addAttribute("governorates", Governorate.values());
        model.addAttribute("selected", governorate);
        model.addAttribute("quote", governorate == null ? null : checkoutService.quote(cart, governorate));
        return "checkout/form";
    }

    @PostMapping("/carts/{number}/checkout")
    public String place(@PathVariable("number") int number,
                        @RequestParam(name = "governorate", required = false) Governorate governorate,
                        RedirectAttributes redirectAttributes) {
        if (governorate == null) {
            throw new StoreException("Choose a governorate.");
        }
        ShoppingCart cart = cartRegistry.get(number);
        Order order = checkoutService.placeOrder(cart, governorate);
        redirectAttributes.addFlashAttribute("message", "Order " + order.orderNumber() + " confirmed.");
        return "redirect:/orders/" + order.orderNumber();
    }
}
