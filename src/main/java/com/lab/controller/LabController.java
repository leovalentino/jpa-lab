package com.lab.controller;

import com.lab.entity.Order;
import com.lab.entity.User;
import com.lab.dto.UserOrderCountDTO;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lab")
public class LabController {
    private final EntityManager entityManager;
    
    public LabController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * N+1 query scenario: Fetch all orders, which triggers EAGER loading of User for each order.
     * This results in 1 query for orders + N queries for users (where N is number of orders).
     */
    @GetMapping("/nplus1")
    @Transactional(readOnly = true)
    public List<Order> nPlus1Demo() {
        // This will execute one query to get all orders
        List<Order> orders = entityManager.createQuery("SELECT o FROM Order o", Order.class)
                                          .getResultList();
        
        // Because Order.user is EAGER, Hibernate will execute additional queries to fetch each User
        // This happens when accessing the user field for each order
        // Let's trigger the loading by accessing user for each order
        for (Order order : orders) {
            // Accessing user triggers a separate query for each order if not already loaded
            // With EAGER, it might still trigger separate queries depending on the fetch strategy
            User user = order.getUser();
            // Do something trivial
            user.getName();
        }
        
        return orders;
    }
    
    /**
     * Dirty Checking demonstration: Update an entity without calling .save()
     */
    @GetMapping("/dirty-checking")
    @Transactional
    public String dirtyCheckingDemo() {
        // Get the first order
        Order order = entityManager.createQuery("SELECT o FROM Order o WHERE o.id = 1", Order.class)
                                   .getSingleResult();
        
        // Modify the entity within a transaction
        order.setStatus("UPDATED_WITHOUT_SAVE");
        
        // No explicit entityManager.merge() or .persist() call
        // The transaction commit will automatically flush changes due to dirty checking
        
        return "Order status updated to: " + order.getStatus();
    }
    
    /**
     * LazyInitializationException scenario: Access lazy collection outside of @Transactional
     */
    @GetMapping("/lazy-exception")
    public String lazyExceptionDemo() {
        Order order;
        // Fetch order within a transactional method
        order = entityManager.createQuery("SELECT o FROM Order o WHERE o.id = 1", Order.class)
                            .getSingleResult();
        
        // Try to access products (lazy collection) outside of transaction
        // This will throw LazyInitializationException
        try {
            int productCount = order.getProducts().size();
            return "Number of products: " + productCount;
        } catch (Exception e) {
            return "LazyInitializationException caught: " + e.getMessage();
        }
    }
    
    /**
     * A transactional version that works correctly
     */
    @GetMapping("/lazy-correct")
    @Transactional(readOnly = true)
    public String lazyCorrectDemo() {
        Order order = entityManager.createQuery("SELECT o FROM Order o WHERE o.id = 1", Order.class)
                                  .getSingleResult();
        
        // Access lazy collection within transaction
        int productCount = order.getProducts().size();
        return "Number of products (within transaction): " + productCount;
    }
    
    /**
     * Cartesian Explosion demonstration: Using multiple JOIN FETCH can lead to redundant rows
     * This query fetches all users, their orders, and products in each order
     * The result set can be huge due to the Cartesian product
     */
    @GetMapping("/cartesian-explosion")
    @Transactional(readOnly = true)
    public String cartesianExplosionDemo() {
        // This query uses JOIN FETCH for both orders and products
        // For each user, for each order, for each product, a row is generated
        // This can lead to a huge number of redundant rows being transferred
        List<User> users = entityManager.createQuery(
            "SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.orders o " +
            "JOIN FETCH o.products", User.class)
            .getResultList();
        
        // Calculate total number of rows that would have been returned without DISTINCT
        // This is for demonstration purposes
        int userCount = users.size();
        int totalOrders = users.stream()
            .mapToInt(user -> user.getOrders().size())
            .sum();
        int totalProducts = users.stream()
            .flatMap(user -> user.getOrders().stream())
            .mapToInt(order -> order.getProducts().size())
            .sum();
        
        // Without DISTINCT, the number of rows would be userCount * totalOrders * totalProducts
        // But with DISTINCT, Hibernate deduplicates in memory
        return String.format(
            "Cartesian Explosion Demo: Fetched %d users with %d orders and %d product associations. " +
            "Without DISTINCT, the result set could have up to %d rows (user*orders*products).",
            userCount, totalOrders, totalProducts, userCount * Math.max(1, totalOrders) * Math.max(1, totalProducts)
        );
    }
    
    /**
     * JOIN vs JOIN FETCH demonstration:
     * 1. Using JOIN without FETCH still triggers N+1 queries when accessing the collection
     * 2. Using JOIN FETCH loads everything in one query
     */
    @GetMapping("/join-vs-fetch")
    @Transactional(readOnly = true)
    public String joinVsFetchDemo() {
        StringBuilder result = new StringBuilder();
        
        // Method 1: JOIN without FETCH
        result.append("=== JOIN without FETCH ===\n");
        List<User> usersWithJoin = entityManager.createQuery(
            "SELECT u FROM User u JOIN u.orders o", User.class)
            .setMaxResults(5) // Limit to 5 users for demonstration
            .getResultList();
        
        // Access orders to trigger N+1 queries
        int totalOrders1 = 0;
        for (User user : usersWithJoin) {
            totalOrders1 += user.getOrders().size();
        }
        result.append("Fetched ").append(usersWithJoin.size()).append(" users with ").append(totalOrders1).append(" orders.\n");
        result.append("This likely triggered N+1 queries (1 for users + N for orders).\n\n");
        
        // Method 2: JOIN FETCH
        result.append("=== JOIN FETCH ===\n");
        List<User> usersWithFetch = entityManager.createQuery(
            "SELECT DISTINCT u FROM User u JOIN FETCH u.orders o", User.class)
            .setMaxResults(5)
            .getResultList();
        
        int totalOrders2 = 0;
        for (User user : usersWithFetch) {
            totalOrders2 += user.getOrders().size();
        }
        result.append("Fetched ").append(usersWithFetch.size()).append(" users with ").append(totalOrders2).append(" orders.\n");
        result.append("This loaded everything in a single query (no N+1).\n");
        
        return result.toString();
    }
    
    /**
     * DTO Projection demonstration: Fetch only needed data using a constructor expression
     * This avoids fetching the entire entity graph and is more efficient
     */
    @GetMapping("/dto-projection")
    @Transactional(readOnly = true)
    public List<UserOrderCountDTO> dtoProjectionDemo() {
        // Use constructor expression to create DTOs directly from the query
        // This fetches only user name and order count, avoiding loading entire User and Order entities
        List<UserOrderCountDTO> results = entityManager.createQuery(
            "SELECT NEW com.lab.dto.UserOrderCountDTO(u.name, COUNT(o)) " +
            "FROM User u LEFT JOIN u.orders o " +
            "GROUP BY u.id, u.name " +
            "ORDER BY COUNT(o) DESC", UserOrderCountDTO.class)
            .setMaxResults(10) // Limit to top 10 users by order count
            .getResultList();
        
        return results;
    }
}
