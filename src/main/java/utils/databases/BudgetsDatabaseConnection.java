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

    public double getBudgetTotalPrice(int budgetID){
        String sql = "SELECT Precio_Total FROM Presupuestos WHERE ID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, budgetID);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.getDouble("Precio_Total");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }


    public ArrayList<Budget> getBudgets(String budgetSearch) throws SQLException {
        String sql;
        Connection conn = connect();
        PreparedStatement pstmt;

        // Verifica si el budgetSearch es numérico
        if (budgetSearch.matches("\\d+")) {
            // Si es numérico, buscar por número de presupuesto
            sql = "SELECT * FROM Presupuestos WHERE Numero_presupuesto= ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(budgetSearch));
        } else {
            // Si no es numérico, buscar por nombre
            sql = "SELECT * FROM Presupuestos WHERE Nombre_Cliente LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + budgetSearch + "%");
        }

        ResultSet resultSet = pstmt.executeQuery();
        ArrayList<Budget> budgets = new ArrayList<>();

        while (resultSet.next()) {
            Budget budget = new Budget(
                    resultSet.getString("Nombre_Cliente"),
                    resultSet.getString("Fecha"),
                    resultSet.getString("Tipo_Cliente"),
                    resultSet.getInt("Numero_Presupuesto")
            );
            budgets.add(budget);
        }

        pstmt.close();
        conn.close();
        return budgets;
    }

    public ArrayList<Budget> getAllBudgets() {
        String sql = "SELECT * FROM Presupuestos";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {

            ArrayList<Budget> budgets = new ArrayList<>();
            while (resultSet.next()) {
                Budget budget = new Budget(
                        resultSet.getString("Nombre_Cliente"),
                        resultSet.getString("Fecha"),
                        resultSet.getString("Tipo_Cliente"),
                        resultSet.getInt("Numero_Presupuesto")
                );
                budgets.add(budget);
            }
            return budgets;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }

    public int getBudgetID(String budgetName, int budgetNumber) throws SQLException {
        String sql = "SELECT ID FROM Presupuestos WHERE Nombre_Cliente = ? AND Numero_presupuesto = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, budgetName);
        pstmt.setInt(2, budgetNumber);
        ResultSet resultSet = pstmt.executeQuery();
        int budgetID = 0;
        while (resultSet.next()) {
            budgetID = resultSet.getInt("ID");
        }
        pstmt.close();
        conn.close();
        return budgetID;
    }

    public ArrayList<Integer> getSavedProductAmounts(String budgetName, int budgetNumber) throws SQLException {
        ArrayList<Integer> productAmounts = new ArrayList<>();
        String sql = "SELECT CANTIDAD FROM PRESUPUESTO_PRODUCTOS WHERE ID_PRESUPUESTO = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int budgetID = getBudgetID(budgetName, budgetNumber);
        pstmt.setInt(1, budgetID);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            int productAmount = resultSet.getInt("CANTIDAD");
            productAmounts.add(productAmount);
        }
        pstmt.close();
        conn.close();
        return productAmounts;
    }

    public ArrayList<String> getSavedProductNames(String budgetName, int budgetNumber) throws SQLException {
        ArrayList<String> productNames = new ArrayList<>();
        String sql = "SELECT NOMBRE_PRODUCTO FROM PRESUPUESTO_PRODUCTOS WHERE ID_PRESUPUESTO = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int budgetID = getBudgetID(budgetName, budgetNumber);
        pstmt.setInt(1, budgetID);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            String productName = resultSet.getString("NOMBRE_PRODUCTO");
            productNames.add(productName);
        }
        pstmt.close();
        conn.close();
        return productNames;
    }

    public ArrayList<String> getProductObservations(String budgetName, int budgetNumber) throws SQLException {
        ArrayList<String> observations = new ArrayList<>();
        String sql = "SELECT OBSERVACIONES FROM PRESUPUESTO_PRODUCTOS WHERE ID_PRESUPUESTO = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int budgetID = getBudgetID(budgetName, budgetNumber);
        pstmt.setInt(1, budgetID);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            observations.add(resultSet.getString("OBSERVACIONES"));
        }
        pstmt.close();
        conn.close();
        return observations;
    }

    public ArrayList<String> getProductMeasures(String budgetName, int budgetNumber) throws SQLException {
        ArrayList<String> measures = new ArrayList<>();
        String sql = "SELECT MEDIDAS FROM PRESUPUESTO_PRODUCTOS WHERE ID_PRESUPUESTO = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int budgetID = getBudgetID(budgetName, budgetNumber);
        pstmt.setInt(1, budgetID);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            measures.add(resultSet.getString("MEDIDAS"));
        }
        pstmt.close();
        conn.close();
        return measures;
    }

    public ArrayList<Double> getProductPrices(String budgetName, int budgetNumber) throws SQLException {
        ArrayList<Double> prices = new ArrayList<>();
        String sql = "SELECT PRECIO FROM PRESUPUESTO_PRODUCTOS WHERE ID_PRESUPUESTO = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int budgetID = getBudgetID(budgetName, budgetNumber);
        pstmt.setInt(1, budgetID);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            prices.add(resultSet.getDouble("PRECIO"));
        }
        pstmt.close();
        conn.close();
        return prices;
    }

    public ArrayList<String> getSelectedBudgetData(int budgetNumber) {
        String sql = "SELECT * FROM Presupuestos WHERE Numero_presupuesto = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, budgetNumber);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<String> selectedBudgetData = new ArrayList<>();
            while (resultSet.next()) {
                selectedBudgetData.add(String.valueOf(resultSet.getInt("ID")));
                selectedBudgetData.add(resultSet.getString("Nombre_Cliente"));
                selectedBudgetData.add(resultSet.getString("Fecha"));
                selectedBudgetData.add(resultSet.getString("Tipo_Cliente"));
            }
            return selectedBudgetData;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public String getOldClientName(int budgetNumber) {
        String sql = "SELECT Nombre_Cliente FROM Presupuestos WHERE Numero_presupuesto = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, budgetNumber);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.getString("Nombre_Cliente");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }




    public int getNextBudgetNumber() {
        int bnumber = 1;  // Por defecto será 1 si no hay presupuestos en la tabla.
        String sql = "SELECT MAX(Numero_presupuesto) FROM Presupuestos";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            if (resultSet.next()) {
                bnumber = resultSet.getInt(1) + 1;  // Accediendo a la columna por índice.
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return bnumber;
    }

    public ArrayList<Budget> getBudgetsByClientId(int clientId) throws SQLException {
        String sql = "SELECT * FROM Presupuestos WHERE Nombre_Cliente = (SELECT Nombre FROM Clientes WHERE ID = ?)";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        ResultSet resultSet = pstmt.executeQuery();
        ArrayList<Budget> budgets = new ArrayList<>();

        while (resultSet.next()) {
            Budget budget = new Budget(
                    resultSet.getString("Nombre_Cliente"),
                    resultSet.getString("Fecha"),
                    resultSet.getString("Tipo_Cliente"),
                    resultSet.getInt("Numero_Presupuesto")
            );
            budgets.add(budget);
        }

        pstmt.close();
        conn.close();
        return budgets;
    }

    public Budget getOneBudget(int budgetId) throws SQLException {
        String sql = "SELECT * FROM Presupuestos WHERE ID = ?";
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, budgetId);
        ResultSet resultSet = pstmt.executeQuery();

        Budget budget = null;
        if (resultSet.next()) {
            budget = new Budget(
                    resultSet.getString("Nombre_Cliente"),
                    resultSet.getString("Fecha"),
                    resultSet.getString("Tipo_Cliente"),
                    resultSet.getInt("Numero_Presupuesto")
            );
        }

        pstmt.close();
        conn.close();
        return budget;
    }
}
