package presenters.client;

import presenters.StandardPresenter;
import utils.Client;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.entities.Clientes;
import views.client.IClientSearchView;
import views.client.list.IClientListView;
import models.IClientListModel;

import javax.swing.*;
import java.util.ArrayList;

import java.util.logging.Logger;

public class ClientListPresenter extends StandardPresenter {
    private final IClientListView clientListView;
    private final IClientListModel clientListModel;
    private final ClientesDBConnection clientsDatabaseConnection;
    private static Logger LOGGER;

    public ClientListPresenter(IClientListView clientListView, IClientListModel clientListModel) {
        this.clientListView = clientListView;
        view = clientListView;
        this.clientListModel = clientListModel;
        clientsDatabaseConnection = new ClientesDBConnection();
    }

    @Override
    protected void initListeners() {

    }

    public void onSearchViewOpenListButtonClicked() {
        ArrayList<Clientes> clients = clientListModel.getClientsFromDB();
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clientes en la base de datos");
        } else {
            clientListView.showView();
            clientListView.setWorkingStatus();
            clientListView.clearView();
            setClientsOnTable();
            clientListView.setWaitingStatus();
        }
    }

    public void setClientsOnTable() {
        int rowCount = 0;
        int clientID;
        ArrayList<Clientes> clients = new ArrayList<>();

        try {
            clients = clientListModel.getClientsFromDB();
        } catch (Exception e) {
            LOGGER.log(null,"ERROR IN METHOD 'setClientsOnTable' IN CLASS->'ClientListPresenter'",e);
        }

        for(Clientes client : clients) {
            clientID = clientsDatabaseConnection.getClientID(client.getNombre(), client.getTipoCliente());

            clientListView.setIntTableValueAt(rowCount, 0, clientID);
            clientListView.setStringTableValueAt(rowCount, 1, client.getNombre());
            clientListView.setStringTableValueAt(rowCount, 2, client.getDireccion());
            clientListView.setStringTableValueAt(rowCount, 3, client.getLocalidad());
            clientListView.setStringTableValueAt(rowCount, 4, client.getTelefono());
            clientListView.setStringTableValueAt(rowCount, 5, client.getTipoCliente());

            rowCount++;

        }
    }

    /*



    */
}

