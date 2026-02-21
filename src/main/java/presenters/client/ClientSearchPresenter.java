package presenters.client;

import models.IClientModel;
import presenters.StandardPresenter;
import presenters.factory.IPresenterFactory;
import utils.Client;
import utils.databases.hibernate.entities.Clientes;
import views.client.IClientSearchView;

import java.util.ArrayList;
import static utils.MessageTypes.*;

public class ClientSearchPresenter extends StandardPresenter {
    private final IClientSearchView clientSearchView;
    private final IClientModel clientModel;
	private final IPresenterFactory presenterFactory;

    public ClientSearchPresenter(IClientSearchView clientSearchView, IClientModel clientModel, IPresenterFactory presenterFactory) {
        this.clientSearchView = clientSearchView;
        view = clientSearchView;
        this.clientModel = clientModel;
		this.presenterFactory = presenterFactory;
    }

    @Override
    public void start() {
        super.start();
        clientModel.queryCities();
    }

    public void onHomeSearchClientButtonClicked() {
        clientSearchView.clearView();
        clientSearchView.showView();
    }

    @Override
    protected void initListeners() {
        clientModel.addClientSearchSuccessListener(() -> updateClientTable(clientModel.getLastClientsQuery()));
        clientModel.addClientSearchFailureListener(() -> clientSearchView.showMessage(CLIENT_SEARCH_FAILURE));
        clientModel.addClientCreationEmptyFieldListener(() -> clientSearchView.showMessage(ANY_CREATION_EMPTY_FIELDS));
        clientModel.addCitiesFetchingSuccessListener(() -> updateCityComboBox(clientModel.getQueriedCities()));
        clientModel.addCitiesFetchingFailureListener(() -> clientSearchView.showMessage(CITY_FETCH_FAILURE));
        clientModel.addClientCreationSuccessListener(() -> addCityIfNotExists(clientModel.getLastCityAdded()));
    }

    private void updateClientTable(ArrayList<Clientes> clientes) {
        int rowCount = 0;
        for (Clientes cliente : clientes) {
            clientSearchView.setTableValueAt(rowCount, 0, cliente.getNombre());
            clientSearchView.setTableValueAt(rowCount, 1, cliente.getDireccion());
            clientSearchView.setTableValueAt(rowCount, 2, cliente.getLocalidad());
            clientSearchView.setTableValueAt(rowCount, 3, cliente.getTelefono());
            clientSearchView.setTableValueAt(rowCount, 4, cliente.getTipoCliente());
            rowCount++;
        }
    }

    private void updateCityComboBox(ArrayList<String> cities) {
        for (String city : cities) {
            clientSearchView.addCityToComboBox(city);
        }
    }

    private void addCityIfNotExists(String lastCityAdded) {
        if (!clientSearchView.isCityInComboBox(lastCityAdded)) {
            clientSearchView.addCityToComboBox(lastCityAdded);
        }
    }

    public void onSearchButtonClicked() {
        clientSearchView.setWorkingStatus();
        String searchedName = clientSearchView.getnameSearchText();
        String searchedCity = clientSearchView.getSelectedCity();
        clientSearchView.clearTable();
        if (searchedCity.equals("Cualquier localidad")) {
            searchedCity = "";
        }
        clientModel.queryClients(searchedName, searchedCity);
        clientSearchView.setWaitingStatus();
    }

    public void onDeleteClientButtonClicked() {
        int[] selectedRows = getSelectedRowsSafely();
        if (selectedRows.length == 1) {
            deleteOneClient();
        } else {
            clientSearchView.showMessage(CLIENT_DELETION_FAILURE);
        }
    }

    private int[] getSelectedRowsSafely() {
        try {
            return clientSearchView.getClientResultTable().getSelectedRows();
        } catch (Exception e) {
            clientSearchView.showMessage(NO_ROW_SELECTED);
            return new int[0];
        }
    }

    public void deleteOneClient() {
        int selectedRow = clientSearchView.getSelectedTableRow();
        if (selectedRow != -1 && !clientSearchView.getClientStringTableValueAt(selectedRow, 0).isEmpty()) {
            int oneClientID = getOneClientID(selectedRow);
            clientModel.deleteOneClient(oneClientID);
            refreshClientTable();
        } else {
            clientSearchView.showMessage(CLIENT_DELETION_FAILURE);
        }
    }

    private void refreshClientTable() {
        clientSearchView.setWorkingStatus();
        clientSearchView.clearTable();
        String searchedName = clientSearchView.getnameSearchText();
        String searchedCity = clientSearchView.getSelectedCity();
        if (searchedCity.equals("Cualquier localidad")) {
            searchedCity = "";
        }
        clientModel.queryClients(searchedName, searchedCity);
        clientSearchView.deselectAllRows();
        clientSearchView.setWaitingStatus();
    }

    public int getOneClientID(int selectedRow) {
        String clientName = clientSearchView.getClientStringTableValueAt(selectedRow, 0);
        String clientType = clientSearchView.getClientStringTableValueAt(selectedRow, 4);
        if (selectedRow != -1 && !clientName.isEmpty() && !clientType.isEmpty()) {
            return clientModel.getClientID(clientName, clientType.equals("Cliente") ? "Cliente" : "Particular");
        }
        return -1;
    }

	public void onShowBudgetHistoryMenuItemClicked() {
		BudgetHistoryPresenter nextPresenter = presenterFactory.createBudgetHistoryPresenter();
		if (!nextPresenter.setBudgetHistoryTable()) { // Si no hay presupuestos, se muestra un mensaje y no se inicia el siguiente presenter
			clientSearchView.showMessage(CLIENT_BUDGET_NO_BUDGETS);
			return;
		}
		presenterFactory.showBudgetHistoryView();
	}
}
