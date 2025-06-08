
# Spring Boot Online Shop

## Description

This is a simple online shop project built using **Spring Boot** and **PostgreSQL**. The application allows users to browse products, view product details, add items to their shopping cart, and proceed to checkout. It demonstrates fundamental concepts like REST APIs, database interaction, and user authentication.

## Features

- User registration and authentication (JWT-based)
- User login/logout
- Google sign-in with OAuth2
- Forgot password mechanism
- CSRF protection enabled
- Cookie-based session management
- Product browsing and details display
- Shopping cart functionality with user cart page
- Checkout system with PayPal integration
- Order tracking system with real-time status updates
- Admin control page for product and user management
- PostgreSQL database integration

## Technologies Used

- **Spring Boot**: Backend REST API framework
- **PostgreSQL**: Database for storing user data, products, and orders
- **JWT (JSON Web Tokens)**: Authentication and authorization
- **OAuth2 (Google Sign-In)**: Social login integration
- **Spring Security**: Security configuration including CSRF and cookie-based auth
- **Spring Data JPA**: Database interaction
- **PayPal Java SDK**: Payment gateway integration for checkout
- **Thymeleaf**: Templating engine for frontend views

## Installation

### Prerequisites

- JDK 17 or higher
- Maven
- PostgreSQL

### Steps

1. **Clone the repository**:
    ```bash
    git clone https://github.com/abdi-siamak/springboot-online-shop.git
    ```

2. **Set up PostgreSQL**:
    - Install PostgreSQL and set up a database (e.g., `shop_db`).
    - Update the `application.properties` file with your PostgreSQL connection details:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/shop_db
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      spring.jpa.hibernate.ddl-auto=update
      ```
3. **Configure PayPal credentials**:
    - Add your PayPal API client ID and secret to the `application.properties`:
      ```properties
      paypal.client.id=your_paypal_client_id
      paypal.client.secret=your_paypal_secret
      paypal.mode=sandbox  # or 'live' for production

4. **Build the application**:
    ```bash
    mvn clean install
    ```

5. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

The application will run on `http://localhost:8080` by default.

## API Endpoints

### User Authentication

- **POST /api/auth/register**: Register a new user
- **POST /api/auth/login**: Login and obtain a JWT token

### Product Management

- **GET /api/products**: Get a list of all products
- **GET /api/products/{id}**: Get details of a specific product

### Cart

- **POST /api/cart/add**: Add a product to the cart
- **GET /api/cart**: View the current cart contents
- **POST /api/cart/checkout**: Proceed to checkout and place an order

### Order Tracking

- **GET /api/orders/{orderId}/status**: Check the current status of an order (e.g., `Pending`, `Processing`, `Shipped`, `Delivered`)
- **GET /api/orders**: View all orders placed by the current user

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
