package models;

import java.util.*;

import entities.PRESUPUESTO_PRODUCTOS;
import models.listeners.failed.*;
import models.listeners.successful.*;
import entities.Presupuestos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Budget;
import utils.Client;
import utils.Product;
import utils.databases.BudgetsDatabaseConnection;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.ProductsDatabaseConnection;
import utils.databases.hibernate.PresupuestosDBConnection;


public class BudgetModel implements IBudgetModel {
    private static final Logger logger = LoggerFactory.getLogger(BudgetModel.class);

    // LISTENERS FOR BUDGET CREATION SUCCESS AND FAILURE
    private final List<BudgetCreationSuccessListener> budgetCreationSuccessListeners;
    private final List<BudgetCreationFailureListener> budgetCreationFailureListeners;
    private final List<BudgetSearchSuccessListener> budgetSearchSuccessListeners;
    private final List<BudgetSearchFailureListener> budgetSearchFailureListeners;

    // DATABASE CONNECTIONS
    private final BudgetsDatabaseConnection budgetsDBConnection;
    private final ProductsDatabaseConnection productsDBConnection;
    private final ClientsDatabaseConnection clientsDBConnection;
    private final PresupuestosDBConnection presupuestosDBConnection;

    // BUDGETS
    private ArrayList<Budget> budgets;



    // CONSTRUCTOR
    public BudgetModel(BudgetsDatabaseConnection budgetsDBConnection,
                       PresupuestosDBConnection presupuestosDBConnection,
                       ProductsDatabaseConnection productsDBConnection,
                       ClientsDatabaseConnection clientsDBConnection)
    {
        // INITIALIZE LISTENERS
        this.budgetCreationSuccessListeners = new LinkedList<>();
        this.budgetCreationFailureListeners = new LinkedList<>();
        this.budgetSearchSuccessListeners = new LinkedList<>();
        this.budgetSearchFailureListeners = new LinkedList<>();

        // INITIALIZE DATABASE CONNECTIONS
        this.budgetsDBConnection = budgetsDBConnection;
        this.productsDBConnection = productsDBConnection;
        this.clientsDBConnection = clientsDBConnection;
        this.presupuestosDBConnection = presupuestosDBConnection;
    }



    // ---------> METHODS AND FUNCTIONS START HERE <-------------
    // ---------> METHODS AND FUNCTIONS START HERE <-------------



    @Override
    public void addBudgetCreationSuccessListener(BudgetCreationSuccessListener listener) { // ADD BUDGET CREATION SUCCESS LISTENER
        budgetCreationSuccessListeners.add(listener);
    }

    @Override
    public void addBudgetCreationFailureListener(BudgetCreationFailureListener listener) { // ADD BUDGET CREATION FAILURE LISTENER
        budgetCreationFailureListeners.add(listener);
    }

    @Override
    public void addBudgetSearchSuccessListener(BudgetSearchSuccessListener listener) { // ADD BUDGET SEARCH SUCCESS LISTENER
        budgetSearchSuccessListeners.add(listener);
    }

    @Override
    public void addBudgetSearchFailureListener(BudgetSearchFailureListener listener) { // ADD BUDGET SEARCH FAILURE LISTENER
        budgetSearchFailureListeners.add(listener);
    }

    private void notifyBudgetCreationFailure() {
        for (BudgetCreationFailureListener listener : budgetCreationFailureListeners) {
            listener.onFailure();
        }
    }

    private void notifyBudgetSearchSuccess() {
        for (BudgetSearchSuccessListener listener : budgetSearchSuccessListeners) {
            listener.onSuccess();
        }
    }

    private void notifyBudgetSearchFailure() {
        for (BudgetSearchFailureListener listener : budgetSearchFailureListeners) {
            listener.onFailure();
        }
    }



    // GET PRODUCTS
    public ArrayList<Product> getProducts(String productName, String productCategory) {
        try {
            return productsDBConnection.getProducts(productName, productCategory);
        } catch (Exception e) {
            logger.error("Error getting products: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    // GET CLIENTS
    @Override
    public ArrayList<Client> getClients(String name, String city) {
        try {
            return clientsDBConnection.getClientsFromNameAndCity(name, city);
        } catch (Exception e) {
            logger.error("Error getting clients: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    // GET CLIENT ID
    public int getClientID(String clientName, String clientType) {
        try {
            return clientsDBConnection.getClientID(clientName, clientType);
        } catch (Exception e) {
            logger.error("Error getting client ID: {}", e.getMessage(), e);
        }
        return -1;
    }

    public Client getClientByID(int clientID) {
        try {
            return clientsDBConnection.getOneClient(clientID);
        } catch (Exception e) {
            logger.error("Error getting client by ID: {}", e.getMessage(), e);
        }
        return null;
    }



    //CREATE BUDGET:
    @Override
    public Presupuestos createBudget(String budgetName, String budgetDate, String budgetClientType, int budgetNumber, double finalPrice) {
        logger.info("========================================");
        logger.info("CREATING NEW BUDGET");
        logger.info("Name: {}, Date: {}, Type: {}, Number: {}, Price: {}",
            budgetName, budgetDate, budgetClientType, budgetNumber, finalPrice);
        logger.info("========================================");
        try {
            Presupuestos presupuestos = new Presupuestos();
            presupuestos.setNombre_Cliente(budgetName);
            presupuestos.setFecha(budgetDate);
            presupuestos.setTipo_Cliente(budgetClientType);
            presupuestos.setNumero_Presupuesto(budgetNumber);
            presupuestos.setPrecio_Total(finalPrice);

            presupuestosDBConnection.savePresupuesto(presupuestos);
            //presupuestosDBConnection.close();
            logger.info("Budget created successfully!");
            return presupuestos;
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR CREATING BUDGET");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            logger.error("========================================");
            notifyBudgetCreationFailure();
        }
        return null;
    }

    public double getBudgetTotalPrice(int budgetID) {
        try {
            return budgetsDBConnection.getBudgetTotalPrice(budgetID);
        } catch (Exception e) {
            logger.error("Error getting budget total price: {}", e.getMessage(), e);
        }
        return -1;
    }


    //GET NEXT BUDGET NUMBER:
    public int getNextBudgetNumber() {
        try {
            return budgetsDBConnection.getNextBudgetNumber();
        } catch (Exception e) {
            logger.error("Error getting next budget number: {}", e.getMessage(), e);
        }
        return -1;
    }


    //SAVE BUDGET PRODUCTS ON BUDGET_PRODUCTS TABLE:
    public void saveProducts(Presupuestos presupuesto, List<Integer> productAmounts, List<String> productNames, List<String> observations, List<String> productMeasures, List<Double> productPrices) {
        logger.info("========================================");
        logger.info("SAVING BUDGET PRODUCTS");
        logger.info("Budget ID: {}, Number of products: {}", presupuesto.getId(), productNames.size());
        logger.info("========================================");
        try {
            for (int i = 0; i < productNames.size(); i++) {
                logger.info("--- Saving product {} of {} ---", (i + 1), productNames.size());
                PRESUPUESTO_PRODUCTOS presupuesto_productos = new PRESUPUESTO_PRODUCTOS();

                presupuesto_productos.setPresupuesto(presupuesto);
                presupuesto_productos.setNOMBRE_PRODUCTO(productNames.get(i));
                presupuesto_productos.setCANTIDAD(productAmounts.get(i));
                presupuesto_productos.setOBSERVACIONES(observations.get(i));
                presupuesto_productos.setMEDIDAS(productMeasures.get(i));
                presupuesto_productos.setPRECIO(productPrices.get(i));

                presupuestosDBConnection.savePresupuestoProductos(presupuesto_productos);
                logger.info("Product {} saved successfully", (i + 1));
            }
            logger.info("All products saved successfully!");
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR SAVING BUDGET PRODUCTS");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            logger.error("========================================");
            notifyBudgetCreationFailure();
        }
    }



    //GET BUDGET ID:
    @Override
    public int getBudgetID(int budgetNumber, String budgetName) {
        try {
            return budgetsDBConnection.getBudgetID(budgetName, budgetNumber);
        } catch (Exception e) {
            logger.error("Error getting budget ID: {}", e.getMessage(), e);
        }
        return -1;
    }


    // GET CITIES NAME
    @Override
    public ArrayList<String> getCitiesName() {
        try {
            return clientsDBConnection.getCities();
        } catch (Exception e) {
            logger.error("Error getting cities: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    // GET LAST BUDGETS QUERY
    @Override
    public ArrayList<Budget> getLastBudgetsQuery() {
        return budgets;
    }

    @Override
    public void queryBudgets(String budgetSearch) {
        try {
            budgets = budgetsDBConnection.getBudgets(budgetSearch);
            notifyBudgetSearchSuccess();
        } catch (Exception e) {
            logger.error("Error querying budgets: {}", e.getMessage(), e);
            notifyBudgetSearchFailure();
        }
    }

    public int getMaxBudgetID() {
        try {
            return budgetsDBConnection.getMaxBudgetID();
        } catch (Exception e) {
            logger.error("Error getting max budget ID: {}", e.getMessage(), e);
        }
        return -1;
    }


    @Override
    public void deleteOneBudget(int budgetID) {
        try {
            budgetsDBConnection.deleteOneBudget(budgetID);
        } catch (Exception e) {
            logger.error("Error deleting budget: {}", e.getMessage(), e);
        }
    }


    public Client GetOneClientByID(int clientID) {
        try {
            return clientsDBConnection.getOneClient(clientID);
        } catch (Exception e) {
            logger.error("Error getting one client: {}", e.getMessage(), e);
        }
        return null;
    }



    public void deleteBudgetProducts(int budgetID) {
        try {
            budgetsDBConnection.deleteBudgetProducts(budgetID);
        } catch (Exception e) {
            logger.error("Error deleting budget products: {}", e.getMessage(), e);
        }
    }

    // ---------> METHODS AND FUNCTIONS END HERE <-------------
    // ---------> METHODS AND FUNCTIONS END HERE <-------------
}
