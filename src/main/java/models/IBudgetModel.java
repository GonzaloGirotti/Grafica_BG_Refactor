package models;
import utils.databases.hibernate.entities.Presupuestos;
import models.listeners.failed.*;
import models.listeners.successful.*;
import utils.Budget;
import utils.Client;
import utils.Product;

import java.util.ArrayList;
import java.util.List;

public interface IBudgetModel {
    void addBudgetCreationSuccessListener(BudgetCreationSuccessListener listener);
    void addBudgetCreationFailureListener(BudgetCreationFailureListener listener);
    void addBudgetSearchSuccessListener(BudgetSearchSuccessListener listener);
    void addBudgetSearchFailureListener(BudgetSearchFailureListener listener);
    double getBudgetTotalPrice(int budgetID);
    Client GetOneClientByID(int clientID);
    ArrayList<Product> getProducts(String productName, String productCategory);
    ArrayList<Client> getClients(String name, String city);
    int getClientID(String clientName, String clientType);
    Client getClientByID(int clientID);
    Presupuestos createBudget(String budgetName, String budgetDate, String budgetClientType, int budgetNumber, double finalPrice);
    int getNextBudgetNumber();
    void saveProducts(Presupuestos presupuestos, List<Integer> productAmounts, List<String> productNames, List<String> observations, List<String> productMeasures, List<Double> productPrices);
    int getBudgetID(int budgetNumber, String budgetName);
    ArrayList<String> getCitiesName();
    ArrayList<Budget> getLastBudgetsQuery();
    void queryBudgets(String budgetSearch);
    void deleteOneBudget(Presupuestos presupuesto);
    Presupuestos findPresupuestoByID(int presupuestoID);
}
