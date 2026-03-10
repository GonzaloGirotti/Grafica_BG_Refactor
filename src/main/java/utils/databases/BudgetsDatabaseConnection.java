package utils.databases;

import utils.Budget;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetsDatabaseConnection extends DatabaseConnection{


    @Override
    protected void createTable(Connection connection) {
        String budgetSQL =  "CREATE TABLE IF NOT EXISTS Presupuestos (" +
                            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "Nombre_Cliente TEXT NOT NULL," +
                            "Fecha TEXT NOT NULL," +
                            "Tipo_Cliente TEXT NOT NULL CHECK(Tipo_Cliente IN ('Cliente', 'Particular'))," +
                            "Numero_presupuesto INTEGER NOT NULL," +
                            "Precio_Total DOUBLE NOT NULL" +
                            ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(QUERY_TIMEOUT);
            stmt.execute(budgetSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
