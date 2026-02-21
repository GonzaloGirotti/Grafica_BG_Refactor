package utils.databases.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.databases.hibernate.config.MyPersistenceUnitInfo;
import utils.databases.hibernate.entities.Clientes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientesDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(ClientesDBConnection.class);
    private final EntityManager em;
    private final EntityManagerFactory emf;

    public ClientesDBConnection() {
        Map<String, String> properties = new HashMap<>(); // Crea un mapa para las propiedades de configuración de Hibernate
        properties.put("hibernate.show_sql", "true"); // Habilita la impresión de las consultas SQL en la consola
        properties.put("hibernate.format_sql", "true"); // Formatea las consultas SQL para que sean más legibles en la consola

        // Crea una EntityManagerFactory utilizando HibernatePersistenceProvider y la configuración personalizada de MyPersistenceUnitInfo
        emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new MyPersistenceUnitInfo(), properties);

        // Crea un EntityManager a partir de la EntityManagerFactory para interactuar con la base de datos
        em = emf.createEntityManager();
    }

    public void saveCliente(Clientes clientes) {
        try {
            logger.info("=== Attempting to save Cliente ===");
            logger.info("Cliente details - Nombre: {}, Direccion: {}, Localidad: {}, Telefono: {}, TipoCliente: {}",
                clientes.getNombre(),
                clientes.getDireccion(),
                clientes.getLocalidad(),
                clientes.getTelefono(),
                clientes.getTipoCliente());
            em.getTransaction().begin(); // Inicia una nueva transacción
            logger.info("Transaction started");
            em.persist(clientes); // Persiste la entidad Cliente en la base de datos
            logger.info("Entity persisted");
            em.getTransaction().commit(); // Confirma la transacción para guardar los cambios en la base de datos
            logger.info("=== Cliente saved successfully with ID: {} ===", clientes.getID());
        } catch (Exception e) {
            logger.error("=== ERROR saving Cliente ===");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            if (em.getTransaction().isActive()) {
                logger.info("Rolling back transaction");
                em.getTransaction().rollback(); // Si ocurre un error, revierte la transacción para evitar cambios incompletos en la base de datos
            }
            throw e; // Vuelve a lanzar la excepción para que pueda ser manejada por el código que llama a este método
        }
    }

    public ArrayList<Clientes> getAllClientes() {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager
        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática
        CriteriaQuery<Clientes> cq = cb.createQuery(Clientes.class); // Crea una CriteriaQuery para la entidad Clientes
        cq.from(Clientes.class); // Define la raíz de la consulta, que es la entidad Clientes
        return new ArrayList<>(session.createQuery(cq).getResultList()); // Ejecuta la consulta y devuelve los resultados como una lista de Clientes
    }

    public ArrayList<Clientes> searchClientsByNameAndCity(String nombre, String ciudad) {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager
        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática
        CriteriaQuery<Clientes> cq = cb.createQuery(Clientes.class); // Crea una CriteriaQuery para la entidad Clientes
        Root<Clientes> cliente = cq.from(Clientes.class); // Define la raíz de la consulta, que es la entidad Clientes

        List<Predicate> predicates = new ArrayList<>(); // Lista para almacenar los predicados de búsqueda. Cada predicado representa una condición de búsqueda basada en los parámetros proporcionados.

        // Búsqueda parcial por Nombre (CASE INSENSITIVE)
        if (nombre != null && !nombre.isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(cliente.get("Nombre")),
                    "%" + nombre.toLowerCase() + "%"
            ));
        }

        // Búsqueda parcial por Localidad
        if (ciudad != null && !ciudad.isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(cliente.get("Localidad")),
                    "%" + ciudad.toLowerCase() + "%"
            ));
        }

        // Unimos los predicados con un AND (puedes usar cb.or() si prefieres)
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return new ArrayList<>(session.createQuery(cq).getResultList()); // Ejecuta la consulta y devuelve los resultados como una lista de Clientes
    }

    public ArrayList<String> getCities() {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager

        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática

        CriteriaQuery<String> cq = cb.createQuery(String.class); // Crea una CriteriaQuery para el tipo String, ya que solo queremos obtener los nombres de las ciudades

        Root<Clientes> cliente = cq.from(Clientes.class);/* Define la raíz de la consulta, que es la entidad Clientes.
                                                            Esto equivale al FROM Clientes en SQL.
                                                            Le dice a Hibernate que la fuente de datos es la entidad Clientes.*/

        cq.select(cliente.get("Localidad")).distinct(true); // Selecciona el campo "Localidad" y asegura que los resultados sean distintos (sin duplicados)

        return new ArrayList<>(session.createQuery(cq).getResultList()); // Ejecuta la consulta y devuelve los resultados como una lista de Strings (nombres de ciudades)
    }

    public Clientes getOneClient(int clientID) {
        return em.find(Clientes.class, clientID); // Busca y devuelve un cliente por su ID utilizando el método find del EntityManager
    }

    public int getClientID(String name, String clientType) {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager

        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática

        CriteriaQuery<Long> cq = cb.createQuery(Long.class); // Crea una CriteriaQuery para el tipo Long (que es el tipo de ID en la entidad)

        Root<Clientes> cliente = cq.from(Clientes.class); // Define la raíz de la consulta, que es la entidad Clientes

        List<Predicate> predicates = new ArrayList<>(); // Lista para almacenar los predicados de búsqueda

        logger.debug("=== Searching for client with name: '{}' and type: '{}' ===", name, clientType);

        // Búsqueda exacta por Nombre (CASE INSENSITIVE)
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.equal(
                    cb.lower(cliente.get("Nombre")),
                    name.toLowerCase()
            ));
            logger.debug("Added predicate for name: {}", name.toLowerCase());
        }

        // Búsqueda exacta por TipoCliente (sin case conversion ya que debe ser exacto)
        if (clientType != null && !clientType.isEmpty()) {
            predicates.add(cb.equal(cliente.get("TipoCliente"), clientType));
            logger.debug("Added predicate for clientType: {}", clientType);
        }

        // Unimos los predicados con un AND para buscar coincidencias en ambos campos
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.select(cliente.get("ID")); // Selecciona el campo "ID" para obtener el ID del cliente

        List<Long> results = session.createQuery(cq).getResultList(); // Ejecuta la consulta y obtiene los resultados como una lista de IDs

        if (results.isEmpty()) {
            logger.warn("No client found with name: '{}' and type: '{}'", name, clientType); // Log de advertencia cuando no se encuentra el cliente
            logger.warn("Attempting to search all clients to debug...");

            // Debug: buscar todos los clientes
            try {
                CriteriaQuery<Clientes> debugCq = cb.createQuery(Clientes.class);
                debugCq.from(Clientes.class);
                List<Clientes> allClients = session.createQuery(debugCq).getResultList();
                logger.warn("Total clients in database: {}", allClients.size());
                for (Clientes c : allClients) {
                    logger.warn("Client in DB - ID: {}, Nombre: '{}', TipoCliente: '{}'", c.getID(), c.getNombre(), c.getTipoCliente());
                }
            } catch (Exception e) {
                logger.error("Error during debug search", e);
            }

            return -1; // Retorna -1 si no se encuentra ningún cliente que coincida con los criterios de búsqueda exacta
        } else {
            int clientID = results.get(0).intValue();
            logger.info("Client found with ID: {} (name: '{}', type: '{}')", clientID, name, clientType); // Log de información cuando se encuentra el cliente
            return clientID; // Retorna el ID del primer cliente encontrado que coincida exactamente con los criterios de búsqueda
        }
    }

    public void deleteOneClient(int clientID) {
        try {
            Clientes cliente = em.find(Clientes.class, clientID);
            if(cliente != null) {
                em.getTransaction().begin();
                em.remove(cliente);
                em.getTransaction().commit();
                logger.info("Client with ID: {} deleted successfully", clientID); // Log de información cuando se elimina el cliente
            } else {
                logger.warn("No client found with ID: {} to delete", clientID); // Log de advertencia cuando no se encuentra el cliente para eliminar
                }
        } catch (Exception e) {
            logger.error("Error deleting client with ID: {}", clientID, e); // Log de error cuando ocurre una excepción al eliminar el cliente
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Si ocurre un error, revierte la transacción para evitar cambios incompletos en la base de datos
                logger.info("Transaction rolled back due to error while deleting client with ID: {}", clientID);
            }
        }
    }
}
