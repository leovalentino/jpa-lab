package com.lab;

import com.lab.entity.Order;
import com.lab.entity.Product;
import com.lab.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {
    private final EntityManager entityManager;
    private final Random random = new Random(42);

    public DataInitializer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional // Now the proxy is active, and this will open a real transaction
    public void run(String... args) {
        initializeData();
    }

    public void initializeData() {
        // Create 100 users
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User("User " + i, "user" + i + "@example.com");
            entityManager.persist(user);
            users.add(user);
        }

        // Create 50 products
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Product product = new Product(
                "Product " + i,
                "Description for product " + i,
                BigDecimal.valueOf(random.nextDouble() * 100 + 10)
            );
            entityManager.persist(product);
            products.add(product);
        }

        // Create 1000 orders
        for (int i = 0; i < 1000; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Order order = new Order(
                LocalDateTime.now().minusDays(random.nextInt(30)),
                random.nextBoolean() ? "COMPLETED" : "PENDING",
                randomUser
            );

            // Add 1-5 random products to each order
            int productCount = random.nextInt(5) + 1;
            for (int j = 0; j < productCount; j++) {
                Product randomProduct = products.get(random.nextInt(products.size()));
                order.getProducts().add(randomProduct);
            }

            entityManager.persist(order);
        }

        // Flush to ensure data is persisted
        entityManager.flush();
        System.out.println("Data initialization complete: 100 users, 50 products, 1000 orders");
    }
}
