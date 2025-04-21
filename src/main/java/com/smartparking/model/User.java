package com.smartparking.model;

import jakarta.persistence.*;
import java.util.List;
import com.smartparking.util.DatabaseUtil;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String contact;

    // Default constructor required by JPA
    protected User() {}

    public User(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // CRUD Operations
    public static User create(User user) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } finally {
            em.close();
        }
    }

    public static User read(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public static List<User> readAll() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public static User update(User user) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
        } finally {
            em.close();
        }
    }

    public static void delete(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
} 