package presenters.workbudget;

import PdfFormater.WorkBudgetClientPDFConverter;
import PdfFormater.WorkBudgetPDFConverter;
import lombok.Setter;
import models.WorkBudgetModel;
import org.javatuples.Pair;
import presenters.StandardPresenter;
import utils.Client;
import utils.MessageTypes;
import utils.WorkBudgetData;
import utils.databases.hibernate.entities.Clientes;
import views.workbudget.WorkBudgetCreateView;
import views.workbudget.stages.*;

import static presenters.workbudget.WorkBudgetCreationStage.CLIENT_SELECTION;
import static utils.MessageTypes.*;
import static utils.TextUtils.truncateAndRound;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static views.workbudget.stages.FinalPriceReferences.*;

public class WorkBudgetCreatePresenter extends StandardPresenter {
	private final WorkBudgetCreateView workBudgetCreateView;
	private final WorkBudgetPDFConverter workBudgetPDFConverter = new WorkBudgetPDFConverter();
    private final WorkBudgetClientPDFConverter workBudgetClientPDFConverter = new WorkBudgetClientPDFConverter();
	private final WorkBudgetModel workBudgetModel;
	@Setter
	private WorkBudgetCreationStage stage;

	public WorkBudgetCreatePresenter(WorkBudgetCreateView workBudgetCreateView, WorkBudgetModel workBudgetModel) {
		this.workBudgetCreateView = workBudgetCreateView;
		this.workBudgetModel = workBudgetModel;
		stage = CLIENT_SELECTION;
		view = workBudgetCreateView;
		workBudgetCreateView.setBackButton(false);
		loadCities();
	}

	@Override
	protected void initListeners() {
		workBudgetModel.addBudgetCreationSuccessListener(() -> {
			workBudgetCreateView.showMessage(WORK_BUDGET_CREATION_SUCCESS);
			generateDataPDF(false);
			generateClientPDF();
			view.hideView();
			workBudgetCreateView.clearView();
		});
		workBudgetModel.addBudgetCreationFailureListener(() -> {
			workBudgetCreateView.showMessage(MessageTypes.WORK_BUDGET_CREATION_FAILURE);
			workBudgetModel.rollbackWorkBudgetCreation();
		});
		workBudgetModel.addBudgetUpdateSuccessListener(() -> {
			workBudgetCreateView.showMessage(WORK_BUDGET_UPDATE_SUCCESS);
			generateDataPDF(true);
			generateClientPDF();
			view.hideView();
			workBudgetCreateView.clearView();
		});
		workBudgetModel.addBudgetUpdateFailureListener(() -> {
			workBudgetCreateView.showMessage(WORK_BUDGET_UPDATE_FAILURE);
			view.hideView();
		});
	}

	private void loadCities() {
		ArrayList<String> cities = workBudgetModel.getCitiesName();
		workBudgetCreateView.getClientSearchingStage().setCitiesComboBox(cities);
	}

	public void onBackButtonPressed() {
		switch (stage) {
			case CONTENT_LIST -> {
				stage = CLIENT_SELECTION;
				workBudgetCreateView.setBackButton(false);
				workBudgetCreateView.showClientSelectionStage();
			}
			case FINAL_PRICE -> {
				stage = WorkBudgetCreationStage.CONTENT_LIST;
				workBudgetCreateView.showContentListStage();
			}
			case CLIENT_SIDE_INFO -> {
				stage = WorkBudgetCreationStage.FINAL_PRICE;
				workBudgetCreateView.changeButtonToNext();
				workBudgetCreateView.showFinalPriceStage();
			}
		}
	}

	public void onNextButtonPressed(boolean isBeingModified, ContentListStage contentListStage) {
		switch (stage) {
			case CLIENT_SELECTION -> {
				if(workBudgetCreateView.getClientSearchingStage().isTableSelected()){
					stage = WorkBudgetCreationStage.CONTENT_LIST;
					workBudgetCreateView.setBackButton(true);
					workBudgetCreateView.showContentListStage();
					contentListStage.setFocusToField(false);
				}else {
					workBudgetCreateView.showMessage(CLIENT_NOT_SELECTED);
				}
			}
			case CONTENT_LIST -> {
				stage = WorkBudgetCreationStage.FINAL_PRICE;
				workBudgetCreateView.showFinalPriceStage();
				calculateFinalPrice();
				setDepositAndBalance(true, true);
			}
			case FINAL_PRICE -> {
				stage = WorkBudgetCreationStage.CLIENT_SIDE_INFO;
				workBudgetCreateView.changeButtonToGenPDF();
				workBudgetCreateView.showClientSideInfoStage();
			}
			case CLIENT_SIDE_INFO -> {
				if(isBeingModified){
					workBudgetModel.updateWorkBudget(workBudgetCreateView.getBudgetNumberLabelValue(),
							Integer.toString(workBudgetCreateView.getSelectedClientId()),
							workBudgetCreateView.getLogisticsData(),
							workBudgetCreateView.getProfitMargin(),
							workBudgetCreateView.getFinalPrice(),
							workBudgetCreateView.getMaterialsList(),
							workBudgetCreateView.getClientInfoItems(),
                            workBudgetCreateView.getPlacingData()
					);
				}else {
                    workBudgetModel.saveWorkBudget(
                            workBudgetCreateView.getSelectedClientId(),
                            workBudgetCreateView.getMaterialsList(),
                            workBudgetCreateView.getLogisticsData(),
                            workBudgetCreateView.getPlacingData(),
                            workBudgetCreateView.getProfitMargin(),
                            workBudgetCreateView.getFinalPrice(),
                            workBudgetCreateView.getClientInfoItems()
                    );
                }
			}
		}
	}

	private void generateDataPDF(boolean isBeingModified) {
		try {
			workBudgetPDFConverter.generateWorkBill(
					isBeingModified,
					workBudgetCreateView.getBudgetNumberLabelValue(),
					workBudgetCreateView.getSelectedClientId(),
					workBudgetCreateView.getMaterialsList(),
					workBudgetCreateView.getLogisticsData(),
					workBudgetCreateView.getPlacingData(),
					workBudgetCreateView.getDeposit(),
					workBudgetCreateView.getBalanceToPay(),
					workBudgetCreateView.getBudgetCost(),
					workBudgetCreateView.getFinalPrice()
			);
		} catch (Exception e) {
			workBudgetCreateView.showMessage(WORK_BUDGET_PDF_CREATION_FAILURE);
			System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
		}
	}

	private void generateClientPDF(){
		try {
            Pair<String, String> depositAndBalance = new Pair<>(
                    workBudgetCreateView.getDeposit(),
                    workBudgetCreateView.getBalanceToPay()
            );

			workBudgetClientPDFConverter.generateBill(
					workBudgetModel.getClientByID(workBudgetCreateView.getSelectedClientId()),
					workBudgetCreateView.getBudgetNumberLabelValue(),
					workBudgetCreateView.getClientInfoItems(),
					workBudgetCreateView.getFinalPrice(),
                    depositAndBalance
			);
		} catch (Exception e) {
			workBudgetCreateView.showMessage(WORK_BUDGET_PDF_CREATION_FAILURE);
			System.err.println("Error generating Client PDF: " + e.getMessage());
		}
	}

	private boolean updating = false;

	public void onProfitMarginChanged() {
		if (updating) return;

		SwingUtilities.invokeLater(() -> {
			updating = true;
			calculateFinalPrice();
			setDepositAndBalance(true, true);
			updating = false;
		});
	}

	public void onDepositChanged(boolean depositModified) {
		if (updating) return;

		SwingUtilities.invokeLater(() -> {
			updating = true;
			setDepositAndBalance(false, depositModified);
			updating = false;
		});
	}

	private void calculateFinalPrice() {
        ContentListStage contentListStage = workBudgetCreateView.getContentListStage();
        DefaultTableModel materialsTableModel = (DefaultTableModel) contentListStage.getMaterialsTable().getModel();
        double materialsCost = 0.0;
        String value;
        // Calculate total materials cost
        for (int i = 0; i < materialsTableModel.getRowCount(); i++) {
            value = (String) materialsTableModel.getValueAt(i, 1);
            materialsCost += (value.isEmpty() ? 0.0 : Double.parseDouble(value));
        }

        String logisticsCostStr = contentListStage.getTextContentByName(ContentListReferences.LOGISTICS_COST);
        double logisticsCost = logisticsCostStr.isEmpty() ? 0.0 : Double.parseDouble(logisticsCostStr);

        double placingCost = 0.0;
        String name;
        DefaultTableModel placersTableModel = (DefaultTableModel) contentListStage.getPlacersTable().getModel();
        for (int i = 0; i < placersTableModel.getRowCount(); i++) {
            name = (String) placersTableModel.getValueAt(i, 1);
            placingCost += (name.isEmpty() ? 0.0 : Double.parseDouble(name));
        }

		double totalCosts = materialsCost + logisticsCost + placingCost;

		FinalPriceStage finalPriceStage = workBudgetCreateView.getFinalPriceStage();

		finalPriceStage.setTextContentByName(
				MATERIALS_COST, String.format("%.2f", materialsCost)
		);
		finalPriceStage.setTextContentByName(
				LOGISTICS_COST, String.format("%.2f", logisticsCost)
		);
		finalPriceStage.setTextContentByName(
				PLACING_COST, String.format("%.2f", placingCost)
		);
		finalPriceStage.setTextContentByName(
				TOTAL_COSTS, String.format("%.2f", totalCosts)
		);
		String profit = finalPriceStage.getTextContentByName(PROFIT_MARGIN);
		double profitMargin = profit.isEmpty() ? 0.00 : Double.parseDouble(profit);
		double finalPrice = totalCosts + totalCosts * ( profitMargin / 100 );
		;
		finalPriceStage.setTextContentByName(
				FINAL_PRICE, String.format("%.2f", Double.parseDouble(truncateAndRound(Double.toString(finalPrice))))
		);
	}

	private void setDepositAndBalance(boolean defaultValue, boolean depositModified){
        FinalPriceStage finalPriceStage = workBudgetCreateView.getFinalPriceStage();
        String finalPriceStr = finalPriceStage.getTextContentByName(FINAL_PRICE);
        double finalPrice = safeParseDouble(finalPriceStr, 0.0);
		double deposit;
		double balance;

        if (defaultValue){
            deposit = finalPrice * 0.5;
            balance = finalPrice * 0.5;
        } else if (depositModified) {
            String depositStr = finalPriceStage.getTextContentByName(DEPOSIT);
            deposit = safeParseDouble(depositStr, finalPrice * 0.5);
            balance = finalPrice - deposit;
        } else {
            String balanceStr = finalPriceStage.getTextContentByName(BALANCE_TO_PAY);
            balance = safeParseDouble(balanceStr, finalPrice * 0.5);
            deposit = finalPrice - balance;
        }


		finalPriceStage.setTextContentByName(
				DEPOSIT, String.format("%.2f", deposit)
		);
		finalPriceStage.setTextContentByName(
				BALANCE_TO_PAY, String.format("%.2f", balance)
		);
	}

	public void onHomeCreateWorkBudgetButtonClicked() {
		view.showView();
        workBudgetCreateView.setBudgetNumberLabelText("Presupuesto N°: " + workBudgetModel.getNextBudgetNumber());
		stage = CLIENT_SELECTION;
		workBudgetCreateView.setBeingModified(false);
	}

	public void onMaterialEnterPressed(ContentListStage contentListStage) {
		String material = contentListStage.getTextContentByName(ContentListReferences.MATERIAL);
		String materialPrice = contentListStage.getTextContentByName(ContentListReferences.MATERIAL_PRICE);
		if(material.isEmpty()){
			workBudgetCreateView.showMessage(INCOMPLETE_MATERIAL_FIELDS);
		}else {

			contentListStage.addItemToTable(material, materialPrice, false);
			contentListStage.clearInputFields(false);
			contentListStage.setFocusToField(false);
		}
	}

    public void onPlacenEnterPressed(ContentListStage contentListStage) {
        String placer = contentListStage.getTextContentByName(ContentListReferences.PLACER);
        String placingPrice = contentListStage.getTextContentByName(ContentListReferences.PLACING_COST);
        if(placer.isEmpty() || placingPrice.isEmpty()){
            workBudgetCreateView.showMessage(INCOMPLETE_MATERIAL_FIELDS);
        }else {
            contentListStage.addItemToTable(placer, placingPrice, true);
            contentListStage.clearInputFields(true);
            contentListStage.setFocusToField(true);
        }
    }

	public void onInfoItemEnterPressed(ClientSideInfoStage clientSideInfoStage) {
		String description = clientSideInfoStage.getTextContentByName(ContentListReferences.MATERIAL);
		if( description.isEmpty()){
			workBudgetCreateView.showMessage(INCOMPLETE_INFO_FIELDS);
		}else {
			clientSideInfoStage.addInfoItemToTable(description);
			clientSideInfoStage.clearMaterialInputFields();
			clientSideInfoStage.setFocusToMaterialField();
		}
	}

	public void onSearchClientButtonClicked(ClientSearchingStage clientSearchingStage) {
		String city = "";
		city = (String) clientSearchingStage.getSelectedCity();
		String name = clientSearchingStage.getClientNameInput();
		int clientID = -1;

		if (city.equals("Seleccione una ciudad")) {
			city = "";
		}

		ArrayList<Clientes> clients = workBudgetModel.getClients(name, city); // LOCAL VARIABLE -> GET CLIENTS BY NAME AND CITY
		clientSearchingStage.clearClientTable(); // CLEAR CLIENT TABLE
		int rowCount = 0; // ROW COUNT VARIABLE

		// LOOP THROUGH CLIENTS
		for (Clientes client : clients) {
			clientID = workBudgetModel.getClientID(client.getNombre(), client.getTipoCliente()); // GET CLIENT ID

			// SET CLIENT TABLE VALUES
			clientSearchingStage.setClientIntTableValueAt(rowCount, 0, clientID);
			clientSearchingStage.setClientStringTableValueAt(rowCount, 1, client.getNombre());
			clientSearchingStage.setClientStringTableValueAt(rowCount, 2, client.getDireccion());
			clientSearchingStage.setClientStringTableValueAt(rowCount, 3, client.getLocalidad());
			clientSearchingStage.setClientStringTableValueAt(rowCount, 4, client.getTelefono());
			clientSearchingStage.setClientStringTableValueAt(rowCount, 5, client.getTipoCliente());
			rowCount++; // INCREMENT ROW COUNT
		}
	}

	public void loadExistingBudget(int budgetId) {
		stage = CLIENT_SELECTION;
		WorkBudgetData data = workBudgetModel.getWorkBudgetById(budgetId);

		workBudgetCreateView.setBudgetNumberLabelText("Presupuesto N°: " + data.getBudgetNumber());
		int clientID = data.getClientID();

		ClientSearchingStage clientSearchingStage = workBudgetCreateView.getClientSearchingStage();
		clientSearchingStage.setClientIntTableValueAt(0,0, clientID);

		Clientes client = workBudgetModel.getClientByID(clientID);
		clientSearchingStage.setClientStringTableValueAt(0,1,client.getNombre());
		clientSearchingStage.setClientStringTableValueAt(0,2,client.getDireccion());
		clientSearchingStage.setClientStringTableValueAt(0,3,client.getLocalidad());
		clientSearchingStage.setClientStringTableValueAt(0,4,client.getTelefono());
		clientSearchingStage.setClientStringTableValueAt(0,5, client.getTipoCliente());
		clientSearchingStage.selectRow(0);

		workBudgetCreateView.setLogisticsData(data.getLogistics(), data.getLogisticsCost());
		workBudgetCreateView.setProfitMargin(data.getProfit());

		ContentListStage contentListStage = workBudgetCreateView.getContentListStage();
		for (Pair<String, String> material : data.getMaterials()) {
			contentListStage.addItemToTable(material.getValue0(), material.getValue1(), false);
		}
        for(Pair<String, String> placer : data.getPlacers()) {
            contentListStage.addItemToTable(placer.getValue0(), placer.getValue1(), true);
        }

		ClientSideInfoStage clientSideInfoStage = workBudgetCreateView.getClientSideInfoStage();
		for (String description : data.getDescriptions()) {
			clientSideInfoStage.addInfoItemToTable(description);
		}
	}

	public void onTableRightClicked(MouseEvent evt, JTable table, String toEliminate) {
		int row = table.rowAtPoint(evt.getPoint());
		if (row >= 0) {
			table.setRowSelectionInterval(row, row);
		}

		JPopupMenu popup = new JPopupMenu();
		JMenuItem deleteItem = new JMenuItem("Eliminar "+ toEliminate);

		deleteItem.addActionListener(e -> {
			int selected = table.getSelectedRow();
			if (selected >= 0) {
				((DefaultTableModel) table.getModel()).removeRow(selected);
			}
		});

		popup.add(deleteItem);
		popup.show(table, evt.getX(), evt.getY());
	}

    private double safeParseDouble(String s, double defaultValue) {
        if (s == null) return defaultValue;
        s = s.trim();
        if (s.isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
