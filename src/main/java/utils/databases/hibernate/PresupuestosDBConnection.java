package utils.databases.hibernate;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import utils.databases.hibernate.entities.Clientes;
import utils.databases.hibernate.entities.PRESUPUESTO_PRODUCTOS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.databases.hibernate.config.MyPersistenceUnitInfo;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // --- MÉTODO AUXILIAR PARA EVITAR REPETIR TRY/CATCH ---
    private void executeTransaction(Runnable action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.run();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error en transacción: {}", e.getMessage(), e);
            throw e;
        }
    }

    // --- OPERACIONES DE PERSISTENCIA ---

    public void savePresupuesto(Object entity) {
        executeTransaction(() -> em.persist(entity));
        logger.info("Entidad {} guardada exitosamente.", entity.getClass().getSimpleName());
    }

    public void deleteOneBudget(Presupuestos presupuestos) {
        executeTransaction(() -> {
            Presupuestos toDelete = em.find(Presupuestos.class, presupuestos.getId());
            if (toDelete != null) {
                em.remove(toDelete);
                logger.info("Presupuesto con ID {} eliminado exitosamente.", presupuestos.getId());
            } else {
                logger.warn("Presupuesto con ID {} no encontrado para eliminación.", presupuestos.getId());
            }
        });
    }

    // --- CONSULTAS SIMPLES ---

    public Presupuestos findPresupuestoByID(int id) {
        return em.find(Presupuestos.class, id);
    }

    public String getBudgetClientName(int budgetNumber){
        String clientName = em.find(Presupuestos.class, budgetNumber).getNombre_Cliente();
        if(clientName != null) {
            logger.info("Client name found for budget number {}: {}", budgetNumber, clientName);
        } else {
            logger.warn("No client name found for budget number {}", budgetNumber);
        }
        return clientName;
    }

    public int getNextBudgetNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Presupuestos> root = cq.from(Presupuestos.class);

        cq.select(cb.coalesce(cb.max(root.get("Numero_Presupuesto")), 0));
        return em.createQuery(cq).getSingleResult() + 1;
    }

    // --- CONSULTAS DINÁMICAS (CRITERIA) ---

    public ArrayList<Presupuestos> getAllPresupuestos(String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Presupuestos> cq = cb.createQuery(Presupuestos.class);
        Root<Presupuestos> root = cq.from(Presupuestos.class);

        List<Predicate> predicates = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            String term = search.trim();
            if (term.matches("\\d+")) {
                predicates.add(cb.equal(root.get("Numero_Presupuesto"), Integer.parseInt(term)));
            } else {
                predicates.add(cb.like(cb.lower(root.get("Nombre_Cliente")), "%" + term.toLowerCase() + "%"));
            }
            cq.where(predicates.toArray(new Predicate[0])); // Solo aplicamos el filtro si hay un término de búsqueda válido
        }
        return new ArrayList<>(em.createQuery(cq).getResultList());
    }

    public void savePresupuestoProductos(PRESUPUESTO_PRODUCTOS presupuestoProductos) {
        executeTransaction(() -> {
            // Hibernate se encarga de entender la clave compuesta y la relación
            // con la entidad Presupuestos automáticamente.
            em.persist(presupuestoProductos);
        });

        logger.info("Productos del presupuesto guardados correctamente.");
    }

    public int getBudgetID(int number, String clientName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Presupuestos> root = cq.from(Presupuestos.class);

        cq.select(root.get("id")).where(
                cb.equal(root.get("Numero_Presupuesto"), number),
                cb.equal(root.get("Nombre_Cliente"), clientName)
        );

        List<Integer> res = em.createQuery(cq).getResultList();
        return res.isEmpty() ? -1 : res.get(0);
    }

    public ArrayList<Presupuestos> getClientBudgets(int clientID) {

        // Join con Clientes para obtener presupuestos del cliente específico
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Presupuestos> cq = cb.createQuery(Presupuestos.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);
        Root<Clientes> cliente = cq.from(Clientes.class);

        cq.select(presupuesto)
                .where(cb.equal(presupuesto.get("Nombre_Cliente"), cliente.get("Nombre_Cliente")),
                        cb.equal(cliente.get("id"), clientID));

        return new ArrayList<>(em.createQuery(cq).getResultList());
    }

    public double getBudgetTotalPrice(int budgetID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<Presupuestos> root = cq.from(Presupuestos.class);

        // Selecciona el precio total, si no existe el ID devuelve 0.0
        cq.select(cb.coalesce(root.get("Precio_Total"), 0.0))
                .where(cb.equal(root.get("id"), budgetID));

        List<Double> result = em.createQuery(cq).getResultList();
        return result.isEmpty() ? 0.0 : result.get(0);
    }

    public ArrayList<String> getBudgetData(int budgetNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Presupuestos> root = cq.from(Presupuestos.class);

        // Seleccionamos múltiples columnas
        cq.multiselect(
                root.get("Nombre_Cliente"),
                root.get("Fecha"),
                root.get("Tipo_Cliente"),
                root.get("Numero_Presupuesto"),
                root.get("Precio_Total")
        ).where(cb.equal(root.get("Numero_Presupuesto"), budgetNumber));

        List<Object[]> result = em.createQuery(cq).getResultList();
        ArrayList<String> dataList = new ArrayList<>();

        if (!result.isEmpty()) {
            Object[] row = result.get(0);
            for (Object col : row) {
                dataList.add(String.valueOf(col)); // Convertimos cada campo a String
            }
        }

        return dataList;
    }

    public void close() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }
}
