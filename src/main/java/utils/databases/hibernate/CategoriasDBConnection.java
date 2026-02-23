package utils.databases.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.databases.hibernate.entities.Categorias;

public class CategoriasDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(ProductosDBConnection.class);
    private final EntityManager em;
    private final EntityManagerFactory emf;

    public CategoriasDBConnection() {
        emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        em = emf.createEntityManager();
    }

    public Categorias getOneCategory(int categoryID) {
        try {
            return em.find(Categorias.class, (long) categoryID);
        } catch (Exception e) {
            logger.error("Error al obtener la categoría con ID {}: {}", categoryID, e.getMessage(), e);
            return null;
        }
    }

    public Long getCategoriaID(String categoryName) {
        try {
            return (em.find(Categorias.class, categoryName)).getID();
        } catch (Exception e) {
            logger.error("Error al obtener la categoría ID del nombre {}. Error {}", categoryName, e.getMessage(), e);
        }
        return null;
    }
}
