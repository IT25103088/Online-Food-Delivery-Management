# Component 04 - Order Management

This zip contains the complete Java/Jakarta Servlet code for **Component 04: Order Management** of the Online Food Delivery System.

## Website broken into 6 parts

1. **User Management** - registration, login, profile, dashboard.
2. **Restaurant Management** - add, view, edit, and remove restaurants.
3. **Menu and Food Item Management** - add, display, update, and remove food items.
4. **Order Management** - cart, checkout, order tracking, order history, status update, and cancel/delete.
5. **Payment Management** - payment gateway, confirmation, transaction history.
6. **Feedback and Review Management** - submit, view, edit, and admin review moderation.

## Included for component 4

- `CartServlet.java` - Cart Page. Add/update/remove/clear cart items.
- `CheckoutServlet.java` - Checkout Page. Creates order records from cart items.
- `OrderServlet.java` - Order Tracking Page and Order History Page. Reads, updates, and deletes/cancels orders.
- `Order.java` and `CartItem.java` - model classes.
- `OrderStore.java`, `FileHandler.java`, `SessionUtil.java`, `HtmlUtil.java` - helper classes.
- `DemoLoginServlet.java` and `LogoutServlet.java` - demo access helpers so the component can run separately.
- `order-management.css` - UI styling for the four required pages.

## CRUD coverage

- **Create**: Checkout creates new order records and stores them in `orders.txt`.
- **Read**: Order tracking/history reads order records from `orders.txt`.
- **Update**: Worker/admin can update order status: `Placed`, `Preparing`, `Out for Delivery`, `Delivered`.
- **Delete**: Customer can cancel before preparation; admin can delete any order.

## Data storage

Orders are saved at:

```text
~/FoodDeliveryData/orders.txt
```

Each order record is stored in this pipe-delimited format:

```text
orderId|username|restaurantName|itemName|quantity|totalPrice|deliveryAddress|status|createdAt
```

## How to run

1. Open the folder in IntelliJ IDEA or any Java IDE.
2. Build with Maven:

```bash
mvn clean package
```

3. Deploy the generated WAR file from `target/component4-order-management.war` to Tomcat 10+.
4. Open:

```text
http://localhost:8080/component4-order-management/
```

Use the demo buttons on the landing page:

- Customer Demo - opens the cart with sample cart items.
- Worker Demo - opens the order tracking page for status updates.
- Admin Demo - opens the order tracking page with delete permission.

## Integration notes

To integrate into the original FoodDelivery project, copy these folders into the existing project and keep package names unchanged:

```text
src/main/java/com/example/model
src/main/java/com/example/servlet
src/main/java/com/example/util
src/main/webapp/css/order-management.css
```

If your existing project already has `FileHandler` or `SessionUtil`, merge only the methods you need or keep your original versions.
