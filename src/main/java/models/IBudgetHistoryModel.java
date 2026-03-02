package models;

import utils.Budget;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;

public interface IBudgetHistoryModel {
    ArrayList<Presupuestos> getClientBudgets(int clientId);
    double getBudgetTotal(int budgetNumber, String clientName);
    int getBudgetID(String clientName, int budgetNumber);
    Presupuestos getOneBudget(int budgetId);
}
