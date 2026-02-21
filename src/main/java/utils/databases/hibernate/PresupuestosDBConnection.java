package utils.databases.hibernate;

import entities.PRESUPUESTO_PRODUCTOS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.MyPersistenceUnitInfo;
import entities.Presupuestos;

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
