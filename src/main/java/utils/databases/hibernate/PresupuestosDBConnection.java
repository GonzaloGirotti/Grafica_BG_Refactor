package utils.databases.hibernate;

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
    private final ClientesDBConnection clientesDBConnection;

    public PresupuestosDBConnection() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new MyPersistenceUnitInfo(), properties);
        em = emf.createEntityManager();
        clientesDBConnection = new ClientesDBConnection();
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

    public ArrayList<Presupuestos> getClientBudgets(int clientID) {
        // Join con Clientes para obtener presupuestos del cliente específico
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Presupuestos> cq = cb.createQuery(Presupuestos.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);
        Root<Clientes> cliente = cq.from(Clientes.class);

        cq.select(presupuesto)
                  .where(cb.equal(presupuesto.get("Nombre_Cliente"), cliente.get("Nombre_Cliente")),
                          cb.equal(cliente.get("id"), clientID));

        return new ArrayList<>(session.createQuery(cq).getResultList());
    }

    public ArrayList<Presupuestos> getAllPresupuestos(String budgetSearch) {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager
        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática
        CriteriaQuery<Presupuestos> cq =  cb.createQuery(Presupuestos.class); // Crea una CriteriaQuery para la entidad Presupuestos
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class); // Define la raíz de la consulta, que es la entidad Presupuestos

        // Lista para almacenar los predicados de búsqueda.
        // Cada predicado representa una condición de búsqueda basada en los parámetros proporcionados.
        List<Predicate> predicates = new ArrayList<>();

        if(budgetSearch != null && !budgetSearch.equals("")) {
            if(budgetSearch.matches("\\d+")) {
                // Agrega un predicado para buscar por número de presupuesto utilizando una comparación de igualdad.
                predicates.add(cb.equal(presupuesto.get("Numero_Presupuesto"), Integer.parseInt(budgetSearch)));
            } else {
                // Agrega un predicado para buscar por nombre de cliente utilizando una comparación "like" que ignora mayúsculas y minúsculas.
                predicates.add(cb.like(cb.lower(presupuesto.get("Nombre_Cliente")), "%" + budgetSearch + "%" ));
            }
        } else {
            // Si no se proporciona un término de búsqueda, se agrega un predicado que siempre es verdadero para obtener todos los presupuestos.
            predicates.add(cb.conjunction()); // Esto es equivalente a "WHERE 1=1" en SQL, lo que significa que no se aplican filtros.
        }

        // Ejecuta la consulta con los predicados acumulados y devuelve los resultados como una lista de Presupuestos.
        // new Predicate[0] se utiliza para convertir la lista de predicados a un array, que es el formato requerido por el método where().
        return new ArrayList<>(session.createQuery(cq.where(predicates.toArray(new Predicate[0]))).getResultList());
    }

    public int getBudgetID(int budgetNumber, String budgetName) {
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);

        cq.select(presupuesto.get("id"))
          .where(
              cb.and(
                  cb.equal(presupuesto.get("Numero_Presupuesto"), budgetNumber),
                  cb.equal(presupuesto.get("Nombre_Cliente"), budgetName)
              )
          );

        List<Integer> result = session.createQuery(cq).getResultList();
        return result.isEmpty() ? -1 : result.get(0); // Devuelve -1 si no se encuentra ningún presupuesto, o el ID del presupuesto encontrado.
    }

    public int getNextBudgetNumber() {
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);

        // Utiliza la función COALESCE para obtener el número máximo de presupuesto, o 0 si no hay presupuestos en la base de datos.
        cq.select(cb.coalesce(cb.max(presupuesto.get("Numero_Presupuesto")), 0)); // Selecciona el número máximo de presupuesto, o 0 si no hay presupuestos.

        Integer maxBudgetNumber = session.createQuery(cq).getSingleResult(); // Ejecuta la consulta y obtiene el resultado.
        return maxBudgetNumber + 1; // Devuelve el siguiente número de presupuesto incrementando el máximo encontrado.
    }

    public double getBudgetTotalPrice(int budgetID) {
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);

        cq.select(presupuesto.get("Precio_Total"))
          .where(cb.equal(presupuesto.get("id"), budgetID)); // Agrega una condición para filtrar por el ID del presupuesto.

        List<Double> result = session.createQuery(cq).getResultList(); // Ejecuta la consulta y obtiene el resultado.
        return result.isEmpty() ? 0.0 : result.get(0); // Devuelve 0.0 si no se encuentra ningún presupuesto, o el precio total del presupuesto encontrado.
    }

    public ArrayList<String> getBudgetData(int budgetNumber) {
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Presupuestos> presupuesto = cq.from(Presupuestos.class);

        //Obtener todos los campos
        cq.multiselect(
                presupuesto.get("Nombre_Cliente"),
                presupuesto.get("Fecha"),
                presupuesto.get("Tipo_Cliente"),
                presupuesto.get("Numero_Presupuesto"),
                presupuesto.get("Precio_Total")
        ).where(cb.equal(presupuesto.get("Numero_Presupuesto"), budgetNumber)); // Agrega una condición para filtrar por el número de presupuesto.

        List<String> result = session.createQuery(cq).getResultList(); // Ejecuta la consulta y obtiene el resultado.
        return new ArrayList<>(result); // Devuelve los datos del presupuesto como una lista de cadenas.
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
