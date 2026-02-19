package models;

import lombok.Getter;
import models.listeners.failed.ClientSearchFailureListener;
import models.listeners.successful.ClientSearchSuccessListener;
import models.listeners.failed.CitiesFetchingFailureListener;
import models.listeners.failed.ClientCreationFailureListener;
import models.listeners.successful.CitiesFetchingSuccessListener;
import models.listeners.successful.ClientCreationSuccessListener;
import models.listeners.failed.ClientCreationEmptyFieldListener;
import utils.Client;
import utils.databases.ClientsDatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientModel implements IClientModel{

	private final ClientsDatabaseConnection dbConnection;
	private final List<ClientCreationSuccessListener> clientCreationSuccessListeners;
	private final List<ClientCreationFailureListener> clientCreationFailureListeners;
	private final List<ClientSearchSuccessListener> clientSearchSuccessListeners;
	private final List<ClientSearchFailureListener> clientSearchFailureListeners;
	private final List<ClientCreationEmptyFieldListener> clientCreationEmptyFieldListeners;


	private final List<CitiesFetchingSuccessListener> citiesFetchingSuccessListeners;
	private final List<CitiesFetchingFailureListener> citiesFetchingFailureListeners;

	private ArrayList<Client> clients;
	private ArrayList<String> cities;
	@Getter
    private String lastCityAdded;

	public ClientModel(ClientsDatabaseConnection dbConnection) {
		this.dbConnection = dbConnection;
		clients = new ArrayList<>();

		this.clientCreationSuccessListeners = new LinkedList<>();
		this.clientCreationFailureListeners = new LinkedList<>();
		this.clientCreationEmptyFieldListeners = new LinkedList<>();

		this.clientSearchSuccessListeners = new LinkedList<>();
		this.clientSearchFailureListeners = new LinkedList<>();

		this.citiesFetchingSuccessListeners = new LinkedList<>();
		this.citiesFetchingFailureListeners = new LinkedList<>();
	}

	@Override
	public void createClient(String clientName, String clientAddress, String clientCity, String clientPhone, boolean isClient) {
		try {
			dbConnection.insertClient(clientName, clientAddress, clientCity, clientPhone, isClient ? "Cliente" : "Particular");
			lastCityAdded = clientCity;
			notifyClientCreationSuccess();
		} catch (Exception e) {
			notifyClientCreationFailure();
		}
	}

	public void queryClients(String clientName, String clientCity) {
		try {
			clients = dbConnection.getClientsFromNameAndCity(clientName, clientCity);
			
			notifyClientSearchSuccess();
		} catch (SQLException e) {
			notifyClientSearchFailure();
		}
	}
	@Override
	public ArrayList<Client> getLastClientsQuery() {
		return clients;
	}

	@Override
	public void queryCities() {
		try {
			cities = dbConnection.getCities();
			notifyCitiesFetchingSuccess();
		} catch (SQLException e) {
			notifyCitiesFetchingFailure();
		}
	}

	public ArrayList<String> getQueriedCities() {
		return cities;
	}

    @Override
	public void addClientCreationSuccessListener(ClientCreationSuccessListener listener) {
		clientCreationSuccessListeners.add(listener);
	}
	@Override
	public void addClientCreationFailureListener(ClientCreationFailureListener listener) {
		clientCreationFailureListeners.add(listener);
	}

	@Override
	public void addClientSearchSuccessListener(ClientSearchSuccessListener listener) {
		clientSearchSuccessListeners.add(listener);
	}
	@Override
	public void addClientSearchFailureListener(ClientSearchFailureListener listener) {
		clientSearchFailureListeners.add(listener);
	}

	@Override
	public void addCitiesFetchingSuccessListener(CitiesFetchingSuccessListener listener) {
		citiesFetchingSuccessListeners.add(listener);
	}

	@Override
	public void addCitiesFetchingFailureListener(CitiesFetchingFailureListener listener) {
		citiesFetchingFailureListeners.add(listener);
	}

	private void notifyClientCreationSuccess() {
		for (ClientCreationSuccessListener listener : clientCreationSuccessListeners) {
			listener.onSuccess();
		}
	}
	private void notifyClientCreationFailure() {
		for (ClientCreationFailureListener listener : clientCreationFailureListeners) {
			listener.onFailure();
		}
	}

	private void notifyClientSearchSuccess() {
		for (ClientSearchSuccessListener listener : clientSearchSuccessListeners) {
			listener.onSuccess();
		}
	}
	private void notifyClientSearchFailure() {
		for (ClientSearchFailureListener listener : clientSearchFailureListeners) {
			listener.onFailure();
		}
	}
	private void notifyCitiesFetchingSuccess() {
		for (CitiesFetchingSuccessListener listener : citiesFetchingSuccessListeners) {
			listener.onSuccess();
		}
	}
	private void notifyCitiesFetchingFailure() {
		for (CitiesFetchingFailureListener listener : citiesFetchingFailureListeners) {
			listener.onFailure();
		}
	}


	//TEST CAMPOS OBLIGATORIOS AL CREAR CLIENTE
	@Override
	public void addClientCreationEmptyFieldListener(ClientCreationEmptyFieldListener listener) {
		clientCreationEmptyFieldListeners.add(listener);
	}

	@Override
	public void deleteOneClient(int clientID) {
        dbConnection.deleteOneClient(clientID);
    }

	@Override
	public int getClientID(String clientName, String clientType) {
		return dbConnection.getClientID(clientName, clientType);
	}

}
