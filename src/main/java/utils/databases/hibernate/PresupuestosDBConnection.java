package utils.databases.hibernate;

import utils.databases.hibernate.entities.PRESUPUESTO_PRODUCTOS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.databases.hibernate.config.MyPersistenceUnitInfo;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.HashMap;
import java.util.Map;

public class PresupuestosDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(PresupuestosDBConnection.class);
    private final EntityManager em;
    private final EntityManagerFactory emf;

    public PresupuestosDBConnection() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new MyPersistenceUnitInfo(), properties);
        em = emf.createEntityManager();
    }

    public void savePresupuesto(Presupuestos presupuesto) {
        try {
            logger.info("=== Attempting to save Presupuesto ===");
            logger.info("Presupuesto details - Cliente: {}, Fecha: {}, Tipo: {}, Numero: {}, Precio: {}",
                presupuesto.getNombre_Cliente(),
                presupuesto.getFecha(),
                presupuesto.getTipo_Cliente(),
                presupuesto.getNumero_Presupuesto(),
                presupuesto.getPrecio_Total());
            em.getTransaction().begin();
            logger.info("Transaction started");
            em.persist(presupuesto);
            logger.info("Entity persisted");
            em.getTransaction().commit();
            logger.info("=== Presupuesto saved successfully with ID: {} ===", presupuesto.getId());
        } catch (Exception e) {
            logger.error("=== ERROR saving Presupuesto ===");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            if (em.getTransaction().isActive()) {
                logger.info("Rolling back transaction");
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public void savePresupuestoProductos(PRESUPUESTO_PRODUCTOS presupuestoProductos) {
        try {
            logger.info("=== Attempting to save Presupuesto_Productos ===");
            logger.info("Composite Key - ID_PRESUPUESTO: {}, ID_PRESUPUESTO_PRODUCTO: {}",
                presupuestoProductos.getPresupuesto().getId(),
                presupuestoProductos.getID_PRESUPUESTO_PRODUCTO());
            logger.info("Product details - Producto: {}, Cantidad: {}, Precio: {}",
                presupuestoProductos.getNOMBRE_PRODUCTO(),
                presupuestoProductos.getCANTIDAD(),
                presupuestoProductos.getPRECIO());
            em.getTransaction().begin();
            logger.info("Transaction started");
            em.persist(presupuestoProductos);
            logger.info("Entity persisted");
            em.getTransaction().commit();
            logger.info("=== Presupuesto_Productos saved successfully ===");
        } catch (Exception e) {
            logger.error("=== ERROR saving Presupuesto_Productos ===");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            if (em.getTransaction().isActive()) {
                logger.info("Rolling back transaction");
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public void deleteOneBudget(Presupuestos presupuesto) {
        em.getTransaction().begin();
        Presupuestos presupuestoToDelete = em.find(Presupuestos.class, presupuesto.getId());
        if (presupuestoToDelete != null) {
            em.remove(presupuestoToDelete);
            logger.info("Presupuesto with ID {} deleted successfully", presupuesto.getId());
        } else {
            logger.warn("Presupuesto with ID {} not found for deletion", presupuesto.getId());
        }
        em.getTransaction().commit();
    }

    public Presupuestos findPresupuestoByID(int presupuestoID) {
        Presupuestos presupuesto = em.find(Presupuestos.class, presupuestoID);
        if (presupuesto != null) {
            logger.info("Presupuesto found with ID {}: Cliente: {}, Fecha: {}, Tipo: {}, Numero: {}, Precio: {}",
                presupuesto.getId(),
                presupuesto.getNombre_Cliente(),
                presupuesto.getFecha(),
                presupuesto.getTipo_Cliente(),
                presupuesto.getNumero_Presupuesto(),
                presupuesto.getPrecio_Total());
        } else {
            logger.warn("No Presupuesto found with ID {}", presupuestoID);
        }
        return presupuesto;
    }

    public void close() {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        } catch (Exception e) {
            logger.error("Error closing EntityManager or EntityManagerFactory: {}", e.getMessage(), e);
        }
    }
}
