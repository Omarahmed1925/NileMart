## First Stage Questions:-

### Q1-Where is the pricing implementation selected in manual wiring, and how can you switch from standard to promotional pricing?

Q1-ManualWiring (Main) it creates the new StandardPricing
and to switch to promotional pricing, you can change the instantiation of the pricingService to use PromotionalPricing instead of StandardPricing inside it


### Q2-Do you need to modify ProductService to switch between pricing implementations? Why?

Q2-no i don't have to do so since the pricing implementation is injected into the ProductService class through its constructor.


### Q3-What happens to manual wiring if the application grows to 60 classes?

Q3-The manualWiring(main) would be responsible for creating all new 60 classes.


### Q4-What can happen if the SKU of a Product used as a HashMap key is changed?

Q4- changing the sku will result in changing the hashkey so HashMap may fail to find the product


Second Stage Questions:-

### Q1-Which classes are Spring beans, and which are not? Why?

Q1-The Spring beans are AppConfig, StandardPricingService, PromotionalPricingService, InMemoryProductRepository, and ProductService, Because they are registered through Spring annotations and component scanning.
Classes such as Product, Category, Money, Check, and the exception classes are not beans because they are not registered or managed by the Spring container.
Interfaces such as PricingService and ProductRepository are not beans themselves; their implementations are.

### Q2-What happens if @Primary is removed from StandardPricingService?

Q2-Removing @Primary causes a NoUniqueBeanDefinitionException because Spring finds two beans implementing PricingService: standardPricingService and promotionalPricingService,
and cannot determine which one to inject.

### Q3-What does component scanning do, and when are bean instances created?

Q3-Component scan finds beans and creates beanDefinitons for them but it doesnt create instances of the beans.
The Spring container creates instances of the beans based on the bean definitions during the application context initialization.



Checkpoint 3 — Shipping and Notifications:-


## Checkpoint 3 — Shipping and Notifications

### 1. What is the difference between @Primary and @Qualifier?

@Primary selects the default bean when multiple beans implement
the same interface.

@Qualifier selects a specific bean by its qualifier or name.

In ShippingQuoteService, @Primary injects ZoneShippingCalculator
as the normal calculator, while @Qualifier("freeShipping")
injects FreeShippingCalculator.

### 2. What happens if we add a third ShippingCalculator without a selection rule?

Spring may throw NoUniqueBeanDefinitionException if multiple
candidates remain and none is uniquely selected.

If ZoneShippingCalculator is still the only @Primary bean,
Spring will continue selecting it by default.

### 3. What are the shipping results?

Cairo with subtotal 5000.00:
Shipping = 0.00 because the threshold is reached and
Greater Cairo is eligible for free shipping.

Matrouh with subtotal 999999.00:
Shipping = 120.00 because REMOTE is not eligible for free shipping.

### 4. Can we add PushNotificationSender without modifying existing senders?

Yes. We can extend AbstractNotificationSender and implement
channel(), address(), and supports() without modifying
SmsNotificationSender or EmailNotificationSender.

This demonstrates the Open/Closed Principle.

## Checkpoint 4 — Shopping Cart & Bean Lifecycle

### Q1: Why does ProductService return the same object while ShoppingCart returns different objects?

Because the scope is by default singleton, which creates one bean object per application context or container. ShoppingCart uses prototype scope, so it creates a new object for every getBean() call.

### Q2: Does @PostConstruct execute once or every time we call getBean()? Why?

Once, because the scope is singleton, as it's executed on every new bean. But since it's singleton, it will result always in the same bean.

### Q3: What goes wrong if a singleton service keeps a ShoppingCart in a constructor parameter?

It will create it once and users will use the same object, as singleton calls its constructor once, so the cart will be injected once and it uses the same object because prototype creates a new bean during getBean(), not during using an existing one.

A new cart should be requested from the Spring container for each customer using ObjectProvider or getBean().

### Q4: Why do we use getOrDefault(sku, 0) instead of get(sku)?

Because if the SKU doesn't exist, it returns 0, and if it exists, it returns the quantity.

### Q5: Why did we choose LinkedHashMap instead of HashMap?

Because LinkedHashMap preserves the insertion order.



## Checkpoint 6 — Spring MVC

### 1. Which beans are registered (controllers, CartRegistry, WebConfig beans)? Why is StoreConsole absent?

Listing the container's bean definitions after startup shows:

- Controllers: `productController`, `cartController`, `checkoutController`, `orderController`
  (plus `globalExceptionHandler`, the `@ControllerAdvice`).
- `cartRegistry` (singleton) and `shoppingCart` (prototype).
- The three beans declared by `WebConfig`: `templateResolver`, `templateEngine`
  and `thymeleafViewResolver`.
- Many infrastructure beans added by `@EnableWebMvc` (`requestMappingHandlerMapping`,
  `requestMappingHandlerAdapter`, `mvcConversionService`, ...).

`StoreConsole` is absent because it is not annotated with `@Component` (or any stereotype) and is not
declared with `@Bean`, so component scanning never registers it. `Main` creates it by hand with
`new StoreConsole(...)` and passes in the three collaborators it obtains from the container. It does not need
to be a bean: nothing else depends on it, and it needs no container services (no injection target, no lifecycle callbacks).
That keeps `Main` the only place that calls `context.getBean()`.

### 2. Where is a new prototype ShoppingCart requested, and who asks Spring to create it?

In `CartRegistry.open(Customer)`, which calls `shoppingCartProvider.getObject()` on the injected
`ObjectProvider<ShoppingCart>`. `CartRegistry` is the one that asks Spring: the singleton delegates the creation to the
container each time a cart is opened, and Spring builds a fresh `ShoppingCart` (prototype scope) with its own
dependencies injected. The call is triggered by `StoreConsole.createCart()` or `CartController.create()`.
Injecting a `ShoppingCart` directly into the singleton would give every customer the same cart.

### 3. Why does a cart created in the console appear in the browser? What if StoreConsole created its own CartRegistry?

`CartRegistry` is a singleton bean, so `Main` (`context.getBean(CartRegistry.class)`) and the controllers (constructor injection)
receive the very same instance, and therefore the same `ConcurrentHashMap` and the same counter. A cart opened from the console is
immediately visible on `/carts` and `/carts/1`.

If `StoreConsole` did `new CartRegistry(...)`, it would have its own private map and counter. A cart created in the console would not exist
for the web controllers (`/carts/1` would return 404 "Cart 1 was not found."), and web carts would not exist in the console. Both
sides would also hand out the same numbers (1, 2, ...) to different customers.

### 4. What is POST/Redirect/GET and what does F5 do?

A POST that changes state (create cart, add item, place order) does not render a page. It answers with a `302` redirect
(`redirect:/orders/{orderNumber}`) and the browser then issues a GET to the new URL. The one-time confirmation message
travels as a flash attribute: stored in the session, shown on the next page and then removed.

Pressing F5 repeats only the last request, which is the harmless GET of the confirmation page. The order is not created again
(checked: after refreshing `/orders/NM-<date>-00001` the order list still contains one order), and the flash message
disappears because it was consumed. Without PRG, F5 would re-submit the POST and the browser would warn about duplicate form submission, creating a second order.

### 5. What does synchronized do when two tabs submit checkout at the same time? What happens to the losing request?

`CheckoutService.placeOrder()` is `synchronized`, so the check of stock, the stock reduction, saving the order and clearing
the cart execute as one atomic step; only one thread at a time can be inside. Without it, two threads could both see enough
stock and both reduce it (overselling), or the same cart could produce two orders.

The request that enters second waits for the lock, then sees the state left by the winner. If it is the same cart, the cart is
now empty and the request fails with a `StoreException` ("Your cart is empty. Add a product before checking out." - HTTP 400). If it is
another cart that wants more stock than is left, it fails with `OutOfStockException` ("Only 1 available for Wireless Earbuds." - HTTP 409).
Because every line is verified before any stock is reduced, the losing request changes nothing, and it is shown the error page.

### 6. What happens when @Controller is temporarily removed from CartController?

`CartController` is no longer picked up by component scanning, so no `cartController` bean exists and its `@GetMapping` / `@PostMapping`
methods are never registered with the handler mapping. The application still compiles and starts, with no error. Observed:
`/products` still answered 200, but `/carts`, `/carts/new` and `/carts/1` returned Tomcat's default **404** page (no handler found),
and the "Add" forms posting to `/carts/items` would also fail. `@Controller` was restored afterwards.

## Java principles demonstrated (Part A2)

- **Encapsulation and immutability**: private final fields; `Customer`, `Order`, `OrderLine`, `Quote`, `CartView`, `LineView` are records (immutable, compact-constructor validation); `Order` copies its list with `List.copyOf`; `ShoppingCart.getLines()` returns an unmodifiable view.
- **Interfaces and polymorphism**: `PricingService`, `ShippingCalculator`, `NotificationSender`, `ProductRepository`; implementations are swappable without changing their callers.
- **Abstract class / template method**: `AbstractNotificationSender` defines the sending algorithm; SMS, Email and Push supply `channel()`, `address()`, and `supports()`. Open/Closed principle.
- **Default methods**: `NotificationSender.supports()` and `ProductRepository.findBySku()`.
- **Generics**: `Repository<T, ID>`, `Check.found(Optional<T>, Supplier<? extends StoreException>)`.
- **Enums with state and behaviour**: `Category(label)`, `Governorate(label, zone)`, `Zone(fee, freeShippingEligible)` with `governorates()` returning an `EnumSet`.
- **Collections**: `LinkedHashMap` (cart lines keep insertion order), `EnumMap` (category groups in enum order), `ConcurrentHashMap`, `CopyOnWriteArrayList`.
- **Streams, lambdas, method references**: `CartRegistry.views()`, `ProductService.groupByCategory()`, `search(Predicate)`, order-line mapping in `CheckoutService`.
- **Optional instead of null**: finders return `Optional`; `Check.found` turns an empty one into a `NotFoundException`; no `Optional.get()`.
- **Exceptions**: unchecked hierarchy `StoreException` -> `NotFoundException` (404) / `OutOfStockException` (409), the status is polymorphic through `status()`, so `GlobalExceptionHandler` needs no `instanceof` chain. Every message is a complete sentence for the customer.
- **Money with BigDecimal**: scale through `Money.scale` (HALF_UP, 2 decimals), `compareTo()` for threshold comparisons, no `double`.
- **Externalised configuration**: VAT rate, free-shipping threshold, max quantity and discount live in `application.properties` and are injected with `@Value`.
- **Concurrency**: `synchronized` `placeOrder` and `addItem`, `AtomicInteger` counters, `ConcurrentHashMap`, `CopyOnWriteArrayList`.
- **Resource management**: `WebServer implements AutoCloseable`; `Main` closes server and context with try-with-resources; `@PostConstruct` loads the catalog, `@PreDestroy` prints "Catalog closed." and the end-of-day summary.
- **Dependency injection principles**: constructor injection only (Lombok `@RequiredArgsConstructor`), no `@Autowired`, no field injection, `@Primary` / `@Qualifier` for selection, `@Scope("prototype")` + `ObjectProvider` for per-customer carts, `@Value` for properties.
