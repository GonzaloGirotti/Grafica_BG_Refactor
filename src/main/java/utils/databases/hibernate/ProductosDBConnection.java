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
import utils.databases.hibernate.entities.Productos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductosDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(ProductosDBConnection.class);
    private final EntityManager em;
    private final EntityManagerFactory emf;
    private final CategoriasDBConnection categoriasDBConnection;

    public ProductosDBConnection() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new MyPersistenceUnitInfo(), properties);
        em = emf.createEntityManager();
        categoriasDBConnection = new CategoriasDBConnection();
    }

    public void saveProducto(Productos producto) {
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            logger.info("Producto guardado exitosamente: {}", producto.getNombre());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error al guardar el producto: {}", e.getMessage(), e);
        }
    }

    public ArrayList<Productos> getProducts(String productName, String categoria_id) {
        Session session = em.unwrap(Session.class); // Obtiene la sesión de Hibernate a partir del EntityManager
        CriteriaBuilder cb = session.getCriteriaBuilder(); // Obtiene un CriteriaBuilder para construir consultas de manera programática
        CriteriaQuery<Productos> cq = cb.createQuery(Productos.class); // Crea una CriteriaQuery para la entidad Productos
        Root<Productos> producto = cq.from(Productos.class); // Define la raíz de la consulta, que es la entidad Productos

        List<Predicate> predicates = new ArrayList<>();

        if(productName != null && !productName.isEmpty()) {
            predicates.add(cb.like(producto.get("nombre"), "%" + productName.toLowerCase() + "%"));
        }

        if(categoria_id != null && !categoria_id.isEmpty()) {
            predicates.add(cb.equal(producto.get("categoria_id"), "%" + categoria_id.toLowerCase() + "%"));
        }

        if(!predicates.isEmpty()){
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return new ArrayList<>(session.createQuery(cq).getResultList()); // Ejecuta la consulta y devuelve los resultados como una lista de productos
    }
}
