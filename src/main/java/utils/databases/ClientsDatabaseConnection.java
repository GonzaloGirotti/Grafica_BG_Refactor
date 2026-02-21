package utils.databases;

import utils.Client;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClientsDatabaseConnection extends DatabaseConnection {
    private static Logger LOGGER;

    protected void createTable(Connection connection) {
        String clientSQL = "CREATE TABLE IF NOT EXISTS Clientes (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre TEXT NOT NULL," +
                "Direccion TEXT NOT NULL," +
                "Localidad TEXT NOT NULL," +
                "Telefono TEXT NOT NULL," +
                "TipoCliente TEXT NOT NULL CHECK (TipoCliente IN('Cliente', 'Particular'))" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(QUERY_TIMEOUT);
            stmt.execute(clientSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
