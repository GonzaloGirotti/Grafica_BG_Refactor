package presenters.budget;

import models.IBudgetListModel;
import presenters.StandardPresenter;
import utils.Budget;
import utils.databases.BudgetsDatabaseConnection;
import utils.databases.hibernate.PresupuestosDBConnection;
import utils.databases.hibernate.entities.Presupuestos;
import views.budget.IBudgetSearchView;
import views.budget.list.IBudgetListView;
import java.util.logging.Logger;

import javax.swing.*;
import java.util.ArrayList;


public class BudgetListPresenter extends StandardPresenter{
    private final IBudgetListModel budgetListModel;
    private final IBudgetListView budgetListView;
    private IBudgetSearchView budgetSearchView;
    private final PresupuestosDBConnection presupuestosDBConnection;
    private static Logger LOGGER;

    public BudgetListPresenter(PresupuestosDBConnection presupuestosDBConnection, IBudgetListView budgetListView, IBudgetListModel budgetListModel) {
        this.budgetListView = budgetListView;
        view = budgetListView;
        this.budgetListModel = budgetListModel;
        this.presupuestosDBConnection = presupuestosDBConnection;
    }


    @Override
    protected void initListeners() {

    }

    public void onSearchViewOpenListButtonClicked() {
        ArrayList<Presupuestos> presupuestos = budgetListModel.getBudgetsFromDB();
        if (presupuestos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay presupuestos en la base de datos");
        } else {
            budgetListView.showView();
            budgetListView.setWorkingStatus();
            budgetListView.clearView();
            setBudgetsOnTable();
            budgetListView.setWaitingStatus();
        }
    }

    public void setBudgetsOnTable() {
        ArrayList<Presupuestos> presupuestos = budgetListModel.getBudgetsFromDB();
        int rowCount = 0;
        int budgetID = 0;

        for (Presupuestos pres : presupuestos) {
            try {
                budgetID = presupuestosDBConnection.getBudgetID(pres.getNumero_Presupuesto(), pres.getNombre_Cliente());
            } catch (Exception e) {
                LOGGER.log(null, "Error al obtener el ID del presupuesto");
            }

            budgetListView.setIntTableValueAt(rowCount, 0, budgetID);
            budgetListView.setStringTableValueAt(rowCount, 1, pres.getNombre_Cliente());
            budgetListView.setStringTableValueAt(rowCount, 2, pres.getFecha());
            budgetListView.setStringTableValueAt(rowCount, 3, pres.getTipo_Cliente());
            budgetListView.setStringTableValueAt(rowCount, 4, String.valueOf(pres.getNumero_Presupuesto()));
            rowCount++;
        }
    }
}