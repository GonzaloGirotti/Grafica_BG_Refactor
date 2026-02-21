package models;

import models.listeners.failed.ClientCreationEmptyFieldListener;
import models.listeners.failed.ClientSearchFailureListener;
import models.listeners.successful.ClientSearchSuccessListener;
import models.listeners.failed.CitiesFetchingFailureListener;
import models.listeners.failed.ClientCreationFailureListener;
import models.listeners.successful.CitiesFetchingSuccessListener;
import models.listeners.successful.ClientCreationSuccessListener;
import utils.Client;
import utils.databases.hibernate.entities.Clientes;

import java.util.ArrayList;

public interface IClientModel {
	void createClient(String clientName, String clientAddress, String clientCity, String clientPhone, boolean clientType);

	void queryCities();

	void addClientCreationSuccessListener(ClientCreationSuccessListener listener);
	void addClientCreationFailureListener(ClientCreationFailureListener listener);
	void addClientSearchSuccessListener(ClientSearchSuccessListener listener);
	void addClientSearchFailureListener(ClientSearchFailureListener listener);

	void queryClients(String searchedName, String searchedAddress);

	ArrayList<Clientes> getLastClientsQuery();

	void addCitiesFetchingSuccessListener(CitiesFetchingSuccessListener listener);

	void addCitiesFetchingFailureListener(CitiesFetchingFailureListener listener);

	ArrayList<String> getQueriedCities();

	String getLastCityAdded();

	void addClientCreationEmptyFieldListener(ClientCreationEmptyFieldListener listener);

	void deleteOneClient(int clientID);

	int getClientID(String clientName, String clientType);
}
