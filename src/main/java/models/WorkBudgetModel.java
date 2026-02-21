package models;

import models.listeners.failed.WorkBudgetCreationFailureListener;
import models.listeners.failed.WorkBudgetSearchFailureListener;
import models.listeners.failed.WorkBudgetUpdateFailureListener;
import models.listeners.successful.WorkBudgetCreationSuccessListener;
import models.listeners.successful.WorkBudgetSearchSuccessListener;
import models.listeners.successful.WorkBudgetUpdateSuccessListener;
import org.javatuples.Pair;
import utils.Client;
import utils.WorkBudget;
import utils.WorkBudgetData;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.WorkBudgetsDatabaseConnection;
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.entities.Clientes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WorkBudgetModel {

	private final List<WorkBudgetCreationSuccessListener> workBudgetCreationSuccessListeners;
	private final List<WorkBudgetCreationFailureListener> workBudgetCreationFailureListeners;

	private final ClientsDatabaseConnection clientsDBConnection;
	private final ClientesDBConnection clientesConnection;
	private final WorkBudgetsDatabaseConnection budgetsDBConnection;
	private static Logger LOGGER;

	private ArrayList<WorkBudget> budgets;

	private final List<WorkBudgetSearchSuccessListener> workBudgetSearchSuccessListeners;
	private final List<WorkBudgetSearchFailureListener> workBudgetSearchFailureListeners;

	private final List<WorkBudgetUpdateSuccessListener> workBudgetUpdateSuccessListeners;
	private final List<WorkBudgetUpdateFailureListener> workBudgetUpdateFailureListeners;


	public WorkBudgetModel(ClientsDatabaseConnection clientsDBConnection,
						   WorkBudgetsDatabaseConnection budgetsDBConnection,
						   ClientesDBConnection clientesConnection) {
		this.clientsDBConnection = clientsDBConnection;
		this.budgetsDBConnection = budgetsDBConnection;
		this.clientesConnection = clientesConnection;

		this.workBudgetCreationSuccessListeners = new ArrayList<>();
		this.workBudgetCreationFailureListeners = new ArrayList<>();
		this.workBudgetSearchSuccessListeners = new ArrayList<>();
		this.workBudgetSearchFailureListeners = new ArrayList<>();
		this.workBudgetUpdateSuccessListeners = new ArrayList<>();
		this.workBudgetUpdateFailureListeners = new ArrayList<>();

	}

	public ArrayList<String> getCitiesName() {
		try {
			return clientesConnection.getCities();
		} catch (Exception e) {
			LOGGER.log(null, "Error getting cities");
		}
		return new ArrayList<>();
	}

	public ArrayList<Clientes> getClients(String name, String city) {
		try {
			return clientesConnection.searchClientsByNameAndCity(name, city);
		} catch (Exception e) {
			LOGGER.log(null, "Error getting clients");
		}
		return new ArrayList<>();
	}

	public int getClientID(String name, String clientType) {
		try {
			return clientesConnection.getClientID(name, clientType);
		} catch (Exception e) {
			LOGGER.log(null, "Error getting client ID");
		}
		return -1;
	}

	public Clientes getClientByID(int clientID) {
		try {
			Clientes client = clientesConnection.getOneClient(clientID);
			if (client != null) {
				return client;
			}
		} catch (Exception e) {
			LOGGER.log(null, "Error getting client by ID");
		}
		return null;
	}

	public void saveWorkBudget(int selectedClientId, ArrayList<Pair<String, String>> materialsList,
							   Pair<String, String> logisticsData, ArrayList<Pair<String, String>> placingData,
							   String profitMargin, String finalPrice, ArrayList<String> clientInfoItems) {
		try {
			LocalDate fechaActual = LocalDate.now();
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fechaActual.format(formato);
			int newNumber = budgetsDBConnection.insertWorkBudget(Integer.toString(selectedClientId), fechaActual.format(formato),
                                                logisticsData.getValue0(), logisticsData.getValue1(), profitMargin, finalPrice);

			budgetsDBConnection.insertMaterials(materialsList, newNumber);
			budgetsDBConnection.insertDescriptions(clientInfoItems, newNumber);
            budgetsDBConnection.insertPlacers(placingData, newNumber);

			notifyBudgetCreationSuccess();
		} catch (Exception e) {
            e.printStackTrace();
			notifyBudgetCreationFailure();
		}
	}

	public void updateWorkBudget(
			int budgetId,
			String clientID,
			Pair<String, String> logisticsData,
			String profit,
			String total,
			ArrayList<Pair<String, String>> materials,
			ArrayList<String> descriptions,
            ArrayList<Pair<String, String>> placers
	){
		try {
			LocalDate fechaActual = LocalDate.now();
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fechaActual.format(formato);
			budgetsDBConnection.updateWorkBudget(
					budgetId,
					clientID,
					fechaActual.format(formato),
					logisticsData.getValue0(),
					logisticsData.getValue1(),
					profit,
					total,
					materials,
					descriptions,
                    placers
			);
			notifyBudgetUpdateSuccess();
		}catch (SQLException e){
			notifyBudgetUpdateFailure();
		}
	}

	public void deleteWorkBudget(int budgetId) {
		budgetsDBConnection.deleteBudgetById(budgetId);
	}

	public int getNextBudgetNumber() {
		try {
			return budgetsDBConnection.getNextBudgetNumber();
		} catch (Exception e) {
			LOGGER.log(null, "Error getting next budget number");
		}
		return -1;
	}

	private void notifyBudgetCreationSuccess() {
		for (WorkBudgetCreationSuccessListener listener : workBudgetCreationSuccessListeners) {
			listener.onSuccess();
		}
	}

	private void notifyBudgetCreationFailure() {
		for (WorkBudgetCreationFailureListener listener : workBudgetCreationFailureListeners) {
			listener.onFailure();
		}
	}

	private void notifyBudgetUpdateSuccess() {
		for (WorkBudgetUpdateSuccessListener listener : workBudgetUpdateSuccessListeners) {
			listener.onSuccess();
		}
	}

	private void notifyBudgetUpdateFailure() {
		for (WorkBudgetUpdateFailureListener listener : workBudgetUpdateFailureListeners) {
			listener.onFailure();
		}
	}

	public void addBudgetCreationSuccessListener(WorkBudgetCreationSuccessListener listener) { // ADD BUDGET CREATION SUCCESS LISTENER
		workBudgetCreationSuccessListeners.add(listener);
	}

	public void addBudgetCreationFailureListener(WorkBudgetCreationFailureListener listener) { // ADD BUDGET CREATION FAILURE LISTENER
		workBudgetCreationFailureListeners.add(listener);
	}

	public void addBudgetSearchSuccessListener(WorkBudgetSearchSuccessListener listener) { // ADD BUDGET SEARCH SUCCESS LISTENER
		workBudgetSearchSuccessListeners.add(listener);
	}

	public void addBudgetSearchFailureListener(WorkBudgetSearchFailureListener listener) {
		workBudgetSearchFailureListeners.add(listener);
	}

	public void addBudgetUpdateSuccessListener(WorkBudgetUpdateSuccessListener listener) {
		workBudgetUpdateSuccessListeners.add(listener);
	}

	public void addBudgetUpdateFailureListener(WorkBudgetUpdateFailureListener listener) {
		workBudgetUpdateFailureListeners.add(listener);
	}

	public void rollbackWorkBudgetCreation() {
		int id = budgetsDBConnection.getNextBudgetNumber() - 1;
		if (budgetsDBConnection.checkFailedInserts(id)){
			budgetsDBConnection.deleteBudgetById(id);
		}
	}

	public void queryBudgets(String budgetSearch) {
		try {
			budgets = budgetsDBConnection.getBudgets(budgetSearch);
			notifyBudgetSearchSuccess();
		} catch (SQLException e) {
			notifyBudgetSearchFailure();
		}
	}

	public WorkBudgetData getWorkBudgetById(int budgetId) {
		try {
			return budgetsDBConnection.getWorkBudgetData(budgetId);
		} catch (SQLException e) {
			System.out.println("Failed to get Work Budget by ID: " + e.getMessage());
			return null;
		}
	}

	public ArrayList<WorkBudget> getLastBudgetsQuery() {
		return budgets;
	}

	private void notifyBudgetSearchSuccess() {
		for (WorkBudgetSearchSuccessListener listener : workBudgetSearchSuccessListeners) {
			listener.onSuccess();
		}
	}

	private void notifyBudgetSearchFailure() {
		for (WorkBudgetSearchFailureListener listener : workBudgetSearchFailureListeners) {
			listener.onFailure();
		}
	}
}
