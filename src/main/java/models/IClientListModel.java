package models;

import utils.Client;
import utils.databases.hibernate.entities.Clientes;

import java.util.ArrayList;

public interface IClientListModel {
    ArrayList<Clientes> getClientsFromDB();
}
