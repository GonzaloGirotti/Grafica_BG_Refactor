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
import utils.databases.hibernate.entities.PRESUPUESTO_PRODUCTOS;
import utils.databases.hibernate.entities.Presupuestos;

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
        Root<Presupuestos> presupuesto = criteriaQuery.from(Presupuestos.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(presupuestoProducto.get("presupuesto").get("ID"), presupuesto.get("ID")));
        predicates.add(cb.equal(presupuesto.get("Numero_Presupuesto"), budgetNumber));

        criteriaQuery.select(presupuestoProducto.get(columnName)).where(predicates.toArray(new Predicate[0]));

        List<Object> results = em.createQuery(criteriaQuery).getResultList();
        logger.info("Consulta para columna '{}' con presupuesto {} y cliente '{}' retornó {} resultados.", columnName, budgetNumber, clientName, results.size());
        return new ArrayList<>(results);
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
