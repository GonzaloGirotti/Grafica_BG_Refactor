package presenters.budget;

import static utils.MessageTypes.*;

import PdfFormater.IPdfConverter;
import PdfFormater.PdfConverter;
import PdfFormater.Row;
import utils.databases.hibernate.entities.Presupuestos;
import models.IBudgetModel;
import models.IBudgetModifyModel;
import presenters.StandardPresenter;
import utils.Budget;
import utils.Client;
import views.budget.IBudgetCreateView;
import views.budget.IBudgetSearchView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.logging.Logger;


// BudgetSearchPresenter CLASS
public class BudgetSearchPresenter extends StandardPresenter {
    private final IBudgetSearchView budgetSearchView;
    private final IBudgetModel budgetModel;
    private final IBudgetCreateView budgetCreateView;
    private final IBudgetModifyModel budgetModifyModel;
    private BudgetCreatePresenter budgetCreatePresenter;
    private static Logger LOGGER;
    private static final IPdfConverter pdfConverter = new PdfConverter();

    // CONSTRUCTOR
    public BudgetSearchPresenter(IBudgetSearchView budgetSearchView, IBudgetCreateView budgetCreateView, IBudgetModel budgetModel, IBudgetModifyModel budgetModifyModel) {
        this.budgetSearchView = budgetSearchView;
        this.budgetCreateView = budgetCreateView;
        view = budgetSearchView;
        this.budgetModel = budgetModel;
        this.budgetModifyModel = budgetModifyModel;
    }

    //  LISTENERS
    @Override
    protected void initListeners() {
        budgetModel.addBudgetSearchSuccessListener(() -> {
            ArrayList<Budget> budgets = budgetModel.getLastBudgetsQuery();
            int rowCount = 0;
            for (Budget budget : budgets) {
                budgetSearchView.setStringTableValueAt(rowCount, 0, budget.getName());
                budgetSearchView.setStringTableValueAt(rowCount, 1, budget.getDate());
                budgetSearchView.setStringTableValueAt(rowCount, 2, budget.getClientType());
                budgetSearchView.setStringTableValueAt(rowCount, 3, budget.getBudgetNumber());
                rowCount++;
            }
        });

        budgetModel.addBudgetSearchFailureListener(() -> budgetSearchView.showMessage(BUDGET_SEARCH_FAILURE));
    }

    public void onPDFButtonClicked(int budgetNumber) {
        Client client = getSelectedBudgetClient();
        ArrayList<String> productNames = getSavedProductNames(budgetNumber, client.getName());
        ArrayList<Integer> productAmounts = getSavedProductAmounts(budgetNumber, client.getName());
        ArrayList<String> productObservations = budgetModifyModel.getProductObservations(budgetNumber, client.getName());
        ArrayList<String> productMeasures = budgetModifyModel.getProductMeasures(budgetNumber, client.getName());
        ArrayList<Double> productPrices = budgetModifyModel.getProductPrices(budgetNumber, client.getName());
        ArrayList<Row> tableContent = new ArrayList<>();
        double globalBudgetTotalPrice = budgetModel.getBudgetTotalPrice(budgetModel.getBudgetID(budgetNumber, client.getName()));

        for (int i = 0; i < productNames.size(); i++) {
            Row row = new Row(productNames.get(i), productAmounts.get(i), productMeasures.get(i), productObservations.get(i), productPrices.get(i), globalBudgetTotalPrice);
            tableContent.add(row);
        }

        try {
            pdfConverter.generateBill( false, client, budgetNumber, tableContent, globalBudgetTotalPrice);
            budgetSearchView.showMessage(PDF_GENERATION_SUCCESS);
        } catch (Exception e) {
            LOGGER.log(null, "ERROR GENERATING BILL");
        }
    }

    public void onSearchButtonClicked() {
        budgetSearchView.setWorkingStatus();
        String budgetSearch = budgetSearchView.getSearchText();
        budgetSearchView.clearTable();
        budgetModel.queryBudgets(budgetSearch);
        budgetSearchView.setWaitingStatus();
    }


    public Client getSelectedBudgetClient() {
        String selectedBudgetName = budgetSearchView.getSelectedBudgetName();
        String selectedBudgetClientType = budgetSearchView.getSelectedBudgetClientType();
        int clientID = budgetModel.getClientID(selectedBudgetName, selectedBudgetClientType);
        return budgetModel.GetOneClientByID(clientID);
    }


    public ArrayList<String> getSavedProductNames(int budgetNumber, String budgetName) {
        return budgetModifyModel.getSavedProductNames(budgetNumber, budgetName);
    }

    public ArrayList<Integer> getSavedProductAmounts(int budgetNumber, String budgetName) {
        return budgetModifyModel.getSavedProductAmounts(budgetNumber, budgetName);
    }


    public void onCleanTableButtonClicked() {
        budgetSearchView.clearTable();
    }


    public void onHomeSearchBudgetButtonClicked() {
        budgetSearchView.showView();
    }


    public void onDeleteButtonClicked() {
        JTable budgetTable = budgetSearchView.getBudgetResultTable();
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow != -1) {
            if(budgetTable.getValueAt(selectedRow, 0) != null && !budgetTable.getValueAt(selectedRow, 0).equals(""))
            {
                int budgetID = getOneBudgetID();
                Presupuestos presupuesto = budgetModel.findPresupuestoByID(budgetID);
                budgetModel.deleteOneBudget(presupuesto);
                budgetSearchView.showMessage(BUDGET_DELETE_SUCCESS);
                budgetSearchView.clearTable();
                budgetModel.queryBudgets("");
                //budgetSearchView.clearTable();
            } else {    budgetSearchView.showMessage(BUDGET_DELETE_FAILURE);    }
        } else {    budgetSearchView.showMessage(BUDGET_DELETE_FAILURE);    }
    }


    public int getOneBudgetID() {
        int selectedRow = budgetSearchView.getBudgetResultTable().getSelectedRow();
        String selectedBudgetName = "";
        int selectedBudgetNumber = -1;
        int budgetId = -1;

        if (selectedRow != -1) {
            selectedBudgetName = budgetSearchView.getSelectedBudgetName();
            if (selectedBudgetName != null && !selectedBudgetName.isEmpty()) {
                selectedBudgetNumber = Integer.parseInt(budgetSearchView.getStringValueAt(selectedRow, 3));
                budgetId = budgetModel.getBudgetID(selectedBudgetNumber, selectedBudgetName);
            }
        }
        return budgetId;
    }
}
