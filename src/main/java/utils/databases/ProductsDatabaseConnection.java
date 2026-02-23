package utils.databases;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import utils.Product;

public class ProductsDatabaseConnection extends DatabaseConnection {

    private final CategoriesDatabaseConnection categoriesDatabaseConnection = new CategoriesDatabaseConnection();

    @Override
    protected void createTable(Connection connection) {
        String productSQL = "CREATE TABLE IF NOT EXISTS Productos (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre TEXT NOT NULL," +
                "Categoria_ID INTEGER NOT NULL," +
                "FOREIGN KEY(Categoria_ID) REFERENCES Categorias(ID)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(QUERY_TIMEOUT);
            stmt.execute(productSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Product> getProducts(String searchText, String category) throws SQLException {

        String sql = "SELECT * FROM Productos WHERE (Nombre LIKE ?)";
        String sqlWCategory = "SELECT * FROM Productos WHERE (Nombre LIKE ?) AND Categoria_ID = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(category.equals("Seleccione una categoría") ? sql : sqlWCategory)) {
            pstmt.setString(1, "%" + searchText + "%");
            if (!category.equals("Seleccione una categoría")) {
                pstmt.setInt(2, categoriesDatabaseConnection.getCategoryID(category));
            }
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("Nombre"),
                        resultSet.getInt("Categoria_ID")
                );
                products.add(product);
            }

            resultSet.close();
            pstmt.close();
            conn.close();
            return products;
        }
    }

    public int getCategoryID(String productName) throws SQLException {
        String sql = "SELECT Categoria_ID FROM Productos WHERE Nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productName);
            ResultSet resultSet = pstmt.executeQuery();
            int categoryID = -1;
            while (resultSet.next()) {
                categoryID = resultSet.getInt("Categoria_ID");
            }
            resultSet.close();
            pstmt.close();
            conn.close();
            return categoryID;
        }
    }

    public void deleteOneProductFromDB(int productID, boolean isModify) throws SQLException {
            String sql = "DELETE FROM Productos WHERE ID = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, productID);
                pstmt.executeUpdate();
                if(!isModify) {
                    JOptionPane.showMessageDialog(null, "Producto eliminado con éxito!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(e.getMessage());
            }
        }

    public int getProductID(String product) throws SQLException {
        String sql = "SELECT ID FROM Productos WHERE Nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product);
            ResultSet resultSet = pstmt.executeQuery();
            int productID = 0;
            while (resultSet.next()) {
                productID = resultSet.getInt("ID");
            }
            resultSet.close();
            pstmt.close();
            conn.close();
            return productID;
        }
    }

    public ArrayList<Product> getAllProducts() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Productos";
        Connection connection = connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productID = rs.getInt("ID");
                int categoryID= rs.getInt("Categoria_ID");
                String productName = rs.getString("Nombre");

                products.add(new Product(productID, productName, categoryID));
            }
        }

        return products;
    }

    public String getCategoryName(int categoryID) {
        return categoriesDatabaseConnection.getCategoryName(categoryID);
    }

    public Product getOneProduct(int productID) throws SQLException {
        String sql = "SELECT * FROM Productos WHERE ID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productID);
            ResultSet resultSet = pstmt.executeQuery();
            Product product = null;
            while (resultSet.next()) {
                product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("Nombre"),
                        resultSet.getInt("Categoria_ID")
                );
            }
            resultSet.close();
            pstmt.close();
            conn.close();
            return product;
        }
    }

    public void updateProductName(int productID, String newName) throws SQLException {
        String sql = "UPDATE Productos SET Nombre = ? WHERE ID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, productID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}




