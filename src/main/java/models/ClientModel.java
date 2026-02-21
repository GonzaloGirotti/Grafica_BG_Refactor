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
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.entities.Clientes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientModel implements IClientModel{

	private final ClientsDatabaseConnection dbConnection;
	private final ClientesDBConnection cliConnection;

	private final List<ClientCreationSuccessListener> clientCreationSuccessListeners;
	private final List<ClientCreationFailureListener> clientCreationFailureListeners;
	private final List<ClientSearchSuccessListener> clientSearchSuccessListeners;
	private final List<ClientSearchFailureListener> clientSearchFailureListeners;
	private final List<ClientCreationEmptyFieldListener> clientCreationEmptyFieldListeners;


	private final List<CitiesFetchingSuccessListener> citiesFetchingSuccessListeners;
	private final List<CitiesFetchingFailureListener> citiesFetchingFailureListeners;

	private ArrayList<Clientes> clients;
	private ArrayList<String> cities;
	@Getter
    private String lastCityAdded;

	public ClientModel(ClientsDatabaseConnection dbConnection, ClientesDBConnection dbConnection2) {
		this.dbConnection = dbConnection;
		this.cliConnection = dbConnection2;
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
			Clientes cliente = new Clientes();
			cliente.setNombre(clientName);
			cliente.setDireccion(clientAddress);
			cliente.setLocalidad(clientCity);
			cliente.setTelefono(clientPhone);
			cliente.setTipoCliente(isClient ? "Cliente" : "Particular");
			cliConnection.saveCliente(cliente);
			lastCityAdded = clientCity;
			notifyClientCreationSuccess();
		} catch (Exception e) {
			notifyClientCreationFailure();
			System.out.println(e);
		}
	}

	public void queryClients(String clientName, String clientCity) {
		try {
			clients = cliConnection.searchClientsByNameAndCity(clientName, clientCity);
			notifyClientSearchSuccess();
		} catch (Exception e) {
			notifyClientSearchFailure();
		}
	}
	@Override
	public ArrayList<Clientes> getLastClientsQuery() {
		return clients;
	}

	@Override
	public void queryCities() {
		try {
			cities = cliConnection.getCities();
			notifyCitiesFetchingSuccess();
		} catch (Exception e) {
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
        cliConnection.deleteOneClient(clientID);
    }

	@Override
	public int getClientID(String clientName, String clientType) {
		return cliConnection.getClientID(clientName, clientType);
	}

}
