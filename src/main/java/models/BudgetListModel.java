package models;

import utils.Budget;
import utils.databases.BudgetsDatabaseConnection;
import utils.databases.hibernate.PresupuestosDBConnection;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;

public class BudgetListModel implements IBudgetListModel {
    private final PresupuestosDBConnection presupuestosDBConnection;

    public BudgetListModel(PresupuestosDBConnection presupuestosDBConnection) {
        this.presupuestosDBConnection = presupuestosDBConnection;
    }

    @Override
    public ArrayList<Presupuestos> getBudgetsFromDB() {
        return presupuestosDBConnection.getAllPresupuestos("");
    }
}