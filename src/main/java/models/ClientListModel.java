package models;

import utils.Client;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.entities.Clientes;

import java.util.ArrayList;

public class ClientListModel implements IClientListModel {
    private final ClientesDBConnection clientsDBConnection;

    public ClientListModel(ClientesDBConnection clientsDBConnection) {
        this.clientsDBConnection = clientsDBConnection;
    }

    @Override
    public ArrayList<Clientes> getClientsFromDB() {
        return clientsDBConnection.getAllClientes();
    }
}
