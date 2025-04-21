package com.smartparking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;
    
    @Column(name = "amount", nullable = false)
    private double amount;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Default constructor
    public Payment() {
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    // CRUD operations
    public static Payment create(Payment payment) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static Payment read(Long id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static Payment update(Payment payment) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            Payment updatedPayment = em.merge(payment);
            em.getTransaction().commit();
            return updatedPayment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static void delete(Long id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            Payment payment = em.find(Payment.class, id);
            if (payment != null) {
                em.remove(payment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static List<Payment> readAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p", Payment.class);
            return query.getResultList();
        } finally {
            em.close();
            emf.close();
        }
    }
} 