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
import utils.databases.hibernate.entities.PRESUPUESTO_PRODUCTOS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresupuestoProductosDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(PresupuestoProductosDBConnection.class);
    private final EntityManager em;
    private final EntityManagerFactory emf;

    public PresupuestoProductosDBConnection(){
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new MyPersistenceUnitInfo(), properties);
        em = emf.createEntityManager();
    }

    public ArrayList<Object> getPresupuestoProductosData(String columnName, int budgetNumber, String clientName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = cb.createQuery(Object.class);
        Root<PRESUPUESTO_PRODUCTOS> presupuestoProducto =  criteriaQuery.from(PRESUPUESTO_PRODUCTOS.class);

        List<Predicate> predicates = new ArrayList<>();

        if(budgetNumber >= 0 && clientName != null) {
            predicates.add(cb.equal(presupuestoProducto.get(columnName), budgetNumber));
        }

        return new ArrayList<>(em.createQuery(criteriaQuery.where(predicates.toArray(new Predicate[0]))).getResultList());
    }

    public ArrayList<Double> getProductPrices(int budgetNumber, String clientName) {
        return getPresupuestoProductosData("PRECIO", budgetNumber, clientName)
                .stream()
                .map(obj -> (Double) obj)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<String> getProductObservations(int budgetNumber, String clientName) {
        return getPresupuestoProductosData("OBSERVACIONES", budgetNumber, clientName)
                .stream()
                .map(obj -> (String) obj)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    }

    public ArrayList<String> getProductMeasures(int budgetNumber, String clientName) {
        return getPresupuestoProductosData("MEDIDAS", budgetNumber, clientName)
                .stream()
                .map(obj -> (String) obj)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<String> getSavedProductNames(int budgetNumber, String clientName) {
        return getPresupuestoProductosData("NOMBRE_PRODUCTO", budgetNumber, clientName)
                .stream()
                .map(obj -> (String) obj)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<Integer> getSavedProductAmounts(int budgetNumber, String clientName) {
        return getPresupuestoProductosData("CANTIDAD", budgetNumber, clientName)
                .stream()
                .map(obj -> (Integer) obj)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
