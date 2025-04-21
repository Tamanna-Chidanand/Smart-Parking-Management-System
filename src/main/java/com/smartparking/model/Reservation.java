package com.smartparking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "spot_id", nullable = false)
    private Long spotId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
    
    // Default constructor
    public Reservation() {
    }
    
    // Constructor with parameters
    public Reservation(Long spotId, Long userId, Long vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        this.spotId = spotId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "ACTIVE";
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSpotId() {
        return spotId;
    }
    
    public void setSpotId(Long spotId) {
        this.spotId = spotId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // CRUD operations
    public static Reservation create(Reservation reservation) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            return reservation;
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
    
    public static Reservation read(Long id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
            emf.close();
        }
    }
    
    public static Reservation update(Reservation reservation) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            Reservation updatedReservation = em.merge(reservation);
            em.getTransaction().commit();
            return updatedReservation;
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
            Reservation reservation = em.find(Reservation.class, id);
            if (reservation != null) {
                em.remove(reservation);
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
    
    public static List<Reservation> readAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
            return query.getResultList();
        } finally {
            em.close();
            emf.close();
        }
    }
} 