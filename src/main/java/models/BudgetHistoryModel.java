package models;

import utils.Budget;
import utils.Client;
import utils.databases.BudgetsDatabaseConnection;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.hibernate.PresupuestosDBConnection;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;

public class BudgetHistoryModel implements IBudgetHistoryModel {
    private final BudgetsDatabaseConnection budgetsDatabaseConnection;
    private final PresupuestosDBConnection presupuestosDBConnection;

    private ArrayList<Presupuestos> budgets;

    public BudgetHistoryModel(BudgetsDatabaseConnection budgetsDatabaseConnection, PresupuestosDBConnection presupuestosDBConnection) {
        this.presupuestosDBConnection = presupuestosDBConnection;
        this.budgetsDatabaseConnection = budgetsDatabaseConnection;
    }

    public ArrayList<Presupuestos> getClientBudgets(int clientId) {
        try {
            this.budgets = this.presupuestosDBConnection.getClientBudgets(clientId);
            return this.budgets;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Presupuestos getOneBudget(int budgetId) {
        try {
            return this.presupuestosDBConnection.findPresupuestoByID(budgetId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getBudgetID(String clientName, int budgetNumber) {
        try {
            return this.presupuestosDBConnection.getBudgetID(budgetNumber, clientName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public double getBudgetTotal(int budgetNumber, String clientName) {
        try {
            int budgetId = this.presupuestosDBConnection.getBudgetID(budgetNumber, clientName);
            return this.presupuestosDBConnection.getBudgetTotalPrice(budgetId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
