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
