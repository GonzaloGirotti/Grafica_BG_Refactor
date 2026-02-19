package utils.databases;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CategoriesDatabaseConnection extends DatabaseConnection {

    private static Logger LOGGER;

    public String getOneCategoryNameByID(int categoryID) throws SQLException {
        String sql = "SELECT Nombre FROM Categorias WHERE ID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryID);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Nombre");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(null,"ERROR IN METHOD 'getOneCategoryNameByID' IN CLASS->'CategoriesDatabaseConnection'",e);
        }
        return "";
    }

    @Override
    protected void createTable(Connection connection) {
        String categorySQL = "CREATE TABLE IF NOT EXISTS Categorias (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre TEXT NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(QUERY_TIMEOUT);
            stmt.execute(categorySQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertCategories(ArrayList<String> categories) throws SQLException {
        String sql = "INSERT INTO Categorias(Nombre) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String category : categories) {
                pstmt.setString(1, category);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear las categorías.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean categoriesAlreadyInserted() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Categorias";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
            if (resultSet.next()) {
                int count = resultSet.getInt("total");
                return count > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(null,"ERROR IN METHOD 'categoriesAlreadyInserted' IN CLASS->'CategoriesDatabaseConnection'", e);
        }
        return false;
    }

    public ArrayList<String> getCategories() throws SQLException {
        ArrayList<String> categorias = new ArrayList<>();
        String sql = "SELECT Nombre FROM Categorias";
        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categorias.add(resultSet.getString("Nombre"));
            }
        } catch (SQLException e) {
            LOGGER.log(null,"ERROR IN METHOD 'getCategories' IN CLASS->'CategoriesDatabaseConnection'", e);
        }
        return categorias;
    }

    public String getCategoryName(int categoryID) {
        String sql = "SELECT Nombre FROM Categorias WHERE ID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryID);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Nombre");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(null,"ERROR IN METHOD 'getCategoryName' IN CLASS->'CategoriesDatabaseConnection'",e);
        }
        return "";
    }

    public int getCategoryID(String categoryName) throws SQLException {
        return getIdByNameFromTable(categoryName, "Categorias");
    }

    private int getIdByNameFromTable(String name, String table) {
        int id = 0;
        String sql = "SELECT ID FROM " + table + " WHERE Nombre = ?";

        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql)){

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("ID");
            }
        } catch (SQLException e) {
            LOGGER.log(null,"ERROR IN METHOD 'getIdByNameFromTable' IN CLASS->'CategoriesDatabaseConnection'",e);
        }
        return id;
    }


}
