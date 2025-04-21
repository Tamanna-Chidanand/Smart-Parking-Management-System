package com.smartparking.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "spot_number", nullable = false, unique = true)
    private String spotNumber;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "available", nullable = false)
    private boolean available = true;
    
    // Default constructor
    public ParkingSpot() {
    }
    
    // Constructor with parameters
    public ParkingSpot(String spotNumber, String type) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.available = true;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSpotNumber() {
        return spotNumber;
    }
    
    public void setSpotNumber(String spotNumber) {
        this.spotNumber = spotNumber;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // CRUD operations
    public static ParkingSpot create(ParkingSpot spot) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            em.persist(spot);
            em.getTransaction().commit();
            return spot;
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
    
    public static ParkingSpot read(Long id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            return em.find(ParkingSpot.class, id);
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static ParkingSpot update(ParkingSpot spot) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            ParkingSpot updatedSpot = em.merge(spot);
            em.getTransaction().commit();
            return updatedSpot;
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
            ParkingSpot spot = em.find(ParkingSpot.class, id);
            if (spot != null) {
                em.remove(spot);
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
    
    public static List<ParkingSpot> readAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<ParkingSpot> query = em.createQuery("SELECT p FROM ParkingSpot p", ParkingSpot.class);
            return query.getResultList();
        } finally {
            em.close();
            emf.close();
        }
    }
} 