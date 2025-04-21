package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseUtil {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("smartParkingPU");
    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
} 