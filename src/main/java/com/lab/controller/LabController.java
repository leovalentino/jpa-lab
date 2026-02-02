package com.lab.controller;

import com.lab.entity.Order;
import com.lab.entity.User;
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
}
