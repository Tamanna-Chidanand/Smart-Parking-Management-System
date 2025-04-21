package com.smartparking.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;
    
    @Column(name = "make", nullable = false)
    private String make;
    
    @Column(name = "model", nullable = false)
    private String model;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    // Default constructor
    public Vehicle() {
    }
    
    // Constructor with parameters
    public Vehicle(String licensePlate, String make, String model, Long userId) {
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.userId = userId;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    // CRUD operations
    public static Vehicle create(Vehicle vehicle) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            em.persist(vehicle);
            em.getTransaction().commit();
            return vehicle;
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
    
    public static Vehicle read(Long id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            return em.find(Vehicle.class, id);
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static Vehicle update(Vehicle vehicle) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            Vehicle updatedVehicle = em.merge(vehicle);
            em.getTransaction().commit();
            return updatedVehicle;
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
            Vehicle vehicle = em.find(Vehicle.class, id);
            if (vehicle != null) {
                em.remove(vehicle);
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
    
    public static List<Vehicle> readAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
            return query.getResultList();
        } finally {
            em.close();
            emf.close();
        }
    }
} 