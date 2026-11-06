/*
 * Esta parte se ha tomado de: http://javanotepad.blogspot.com/2007/05/jpa-entitymanagerfactory-in-web.html
 */
package com.example;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Clase Singleton que crea y entrega el EntityManagerFactory
 * conectado a la unidad de persistencia de MongoDB (mongoPU).
 *
 * @author 
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;

    private static final PersistenceManager singleton = new PersistenceManager();

    protected EntityManagerFactory emf;

    public static PersistenceManager getInstance() {
        return singleton;
    }

    private PersistenceManager() {
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            createEntityManagerFactory();
        }
        return emf;
    }

    public void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            emf = null;
            if (DEBUG) {
                System.out.println("\n*** Persistence finished at " + new java.util.Date());
            }
        }
    }

    protected void createEntityManagerFactory() {
        // Antes se llamaba "AplicacionMundialPU" (PostgreSQL). Ahora es "mongoPU" (MongoDB).
        this.emf = Persistence.createEntityManagerFactory("mongoPU", System.getProperties());
        if (DEBUG) {
            System.out.println("\n*** Persistence started at " + new java.util.Date());
        }
    }
}