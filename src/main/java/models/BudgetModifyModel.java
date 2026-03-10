package models;
import utils.databases.BudgetsDatabaseConnection;
import utils.databases.hibernate.PresupuestoProductosDBConnection;
import utils.databases.hibernate.PresupuestosDBConnection;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;
import java.util.logging.Logger;

public class BudgetModifyModel implements IBudgetModifyModel {
    private final PresupuestoProductosDBConnection presupuestoProductosDBConnection;
    private final PresupuestosDBConnection presupuestosDBConnection;
    //private static final Logger LOGGER = Logger.getLogger(BudgetModifyModel.class.getName());


    public BudgetModifyModel(PresupuestoProductosDBConnection presupuestoProductosDBConnection, PresupuestosDBConnection presupuestosDBConnection) {
        this.presupuestosDBConnection = presupuestosDBConnection;
        this.presupuestoProductosDBConnection = presupuestoProductosDBConnection;
    }

    public ArrayList<Double> getProductPrices(int budgetNumber, String budgetName) {
        try {
            return presupuestoProductosDBConnection.getProductPrices(budgetNumber, budgetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> getProductObservations(int budgetNumber, String budgetName) {
        try {
            return presupuestoProductosDBConnection.getProductObservations(budgetNumber, budgetName);
        } catch (Exception e) {
            e.printStackTrace();
            //LOGGER.log(null, "Error getting product observations");
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> getProductMeasures(int budgetNumber, String budgetName) {
        try {
            return presupuestoProductosDBConnection.getProductMeasures(budgetNumber, budgetName);
        } catch (Exception e) {
            e.printStackTrace();
            //LOGGER.log(null, "Error getting product measures");
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getSavedProductNames(int budgetNumber, String budgetName) {
        try {
            return presupuestoProductosDBConnection.getSavedProductNames(budgetNumber, budgetName);
        } catch (Exception e) {
            e.printStackTrace();
            //LOGGER.log(null, "Error getting product names");
        }
        return new ArrayList<>();
    }

    public ArrayList<Integer> getSavedProductAmounts(int budgetNumber, String budgetName) {
        try {
            return presupuestoProductosDBConnection.getSavedProductAmounts(budgetNumber, budgetName);
        } catch (Exception e) {
            e.printStackTrace();
            //LOGGER.log(null, "Error getting product amounts");
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getSelectedBudgetData(int budgetNumber) {
        try {
            return presupuestosDBConnection.getBudgetData(budgetNumber);
        } catch (Exception e) {
            e.printStackTrace();
            //LOGGER.log(null, "Error getting selected budget data");
        }
        return new ArrayList<>();
    }

    @Override
    public String getOldClientName(int budgetNumber) {
        return presupuestosDBConnection.getBudgetClientName(budgetNumber);
    }
}