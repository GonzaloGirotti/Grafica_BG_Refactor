package utils.databases.hibernate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.javatuples.Pair;

public interface ICriteriaQuery {

    // Método por defecto para proporcionar CriteriaBuilder, CriteriaQuery y Root para una entidad dada
    // Utilizamos Class<?> para permitir que el método sea genérico y pueda trabajar con cualquier entidad

    public default Pair<CriteriaQuery, Session> ProvideCriteriaAndSession(EntityManager em, Class<?> entities){
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<?> cq = cb.createQuery(entities);
        Root<?> root = cq.from(entities);
        return new Pair<>(cq, session);
    }
}
