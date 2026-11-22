package org.route.controller;

import lombok.RequiredArgsConstructor;
import org.route.Cart.CartRegistry;
import org.route.Cart.ShoppingCart;
import org.route.Service.ProductService;
import org.route.common.StoreException;
import org.route.controller.form.AddItemForm;
import org.route.controller.form.NewCartForm;
import org.route.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartRegistry cartRegistry;
    private final ProductService productService;

    @GetMapping("/carts")
    public String list(Model model) {
        model.addAttribute("carts", cartRegistry.views());
        return "carts/list";
    }

    @GetMapping("/carts/new")
    public String newForm(Model model) {
        model.addAttribute("form", new NewCartForm());
        return "carts/new";
    }

    @PostMapping("/carts")
    public String create(@ModelAttribute("form") NewCartForm form,
                         RedirectAttributes redirectAttributes) {
        String email = form.getEmail();
        if (email != null && email.isBlank()) {
            email = null;
        }
        Customer customer = new Customer(form.getFullName(), form.getPhone(), email);
        ShoppingCart cart = cartRegistry.open(customer);
        int number = cart.getCartNumber();
        redirectAttributes.addFlashAttribute("message",
                "Cart " + number + " created for " + customer.fullName() + ".");
        return "redirect:/carts/" + number;
    }

    @GetMapping("/carts/{number}")
    public String view(@PathVariable("number") int number, Model model) {
        model.addAttribute("cart", cartRegistry.get(number).view());
        model.addAttribute("products", productService.search(p -> p.getStockQuantity() > 0));
        return "carts/view";
    }

    @PostMapping("/carts/items")
    public String addItem(@ModelAttribute AddItemForm form, RedirectAttributes redirectAttributes) {
        if (form.getCartNumber() == null) {
            throw new StoreException("Choose a cart.");
        }
        if (form.getSku() == null || form.getSku().isBlank()) {
            throw new StoreException("Choose a product.");
        }
        if (form.getQuantity() == null) {
            throw new StoreException("Enter a quantity.");
        }
        ShoppingCart cart = cartRegistry.get(form.getCartNumber());
        cart.addItem(form.getSku(), form.getQuantity());
        redirectAttributes.addFlashAttribute("message",
                "Added " + form.getQuantity() + " x " + productService.findBySku(form.getSku()).getName()
                        + " to cart " + form.getCartNumber() + ".");
        return "redirect:/carts/" + form.getCartNumber();
    }
}
