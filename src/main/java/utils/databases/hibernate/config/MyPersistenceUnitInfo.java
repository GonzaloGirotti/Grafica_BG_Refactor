package utils.databases.hibernate.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;

import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class MyPersistenceUnitInfo implements PersistenceUnitInfo {
    @Override
    public String getPersistenceUnitName() {
        return "my-persistence-unit";
    }

    @Override
    public String getPersistenceProviderClassName() { // <- Implement this method to return the fully qualified class name of your persistence provider <provider> tag in persistence.xml
        return "org.hibernate.jpa.HibernatePersistenceProvider";
    }

    @Override
    public String getScopeAnnotationName() {
        return "";
    }

    @Override
    public List<String> getQualifierAnnotationNames() {
        return List.of();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        /*
        // Assuming RESOURCE_LOCAL for simplicity.
        Explain: This means that the application will manage transactions directly, rather than relying on a JTA transaction manager.
        This is often used in standalone applications or when using a simple DataSource without JTA support.
        JTA explain: Java Transaction API (JTA) is a standard Java API for managing transactions in Java applications.
        It allows developers to define and manage transactions across multiple resources, such as databases and message queues, in a consistent way.
        When using JTA, the application server or container manages the transaction boundaries,
        and the application code can participate in these transactions without needing to manage them directly.
        This is particularly useful in enterprise applications that require distributed transactions across multiple resources.
        */
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        return null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        /*
        // <- Implement this method to return a DataSource instance that provides connections to your database.
        This is used when the transaction type is RESOURCE_LOCAL.
        For SQLite, you can create a DataSource using a library like HikariCP or Apache DBCP,
        or you can implement a simple DataSource that returns connections to your SQLite database.
        */

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:sqlite:S:\\Github Local Repositories\\Grafica_BG_Refactor\\GRAFICA_BAHIA_DATABASE.db"); // Set the JDBC URL for your SQLite database
        ds.setDriverClassName("org.sqlite.JDBC"); // Set the JDBC driver class name for SQLite
        ds.setUsername("");
        ds.setPassword("");
        return ds;
    }

    @Override
    public List<String> getMappingFileNames() {
        return List.of();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return List.of();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return List.of(
                "utils.databases.hibernate.entities.Presupuestos",
                "utils.databases.hibernate.entities.PRESUPUESTO_PRODUCTOS"
                );
    }

    @Override
    public List<String> getAllManagedClassNames() {
        return List.of();
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return null;
    }

    @Override
    public FetchType getDefaultToOneFetchType() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "";
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {

    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
