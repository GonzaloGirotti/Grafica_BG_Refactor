package models;

import utils.databases.CategoriesDatabaseConnection;
import utils.databases.hibernate.CategoriasDBConnection;
import utils.databases.hibernate.entities.Categorias;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryModel implements ICategoryModel {

    private final CategoriesDatabaseConnection categoriesDBConnection;
    private final CategoriasDBConnection categoriasDBConnection;

    public CategoryModel(CategoriesDatabaseConnection categoriesDBConnection, CategoriasDBConnection categoriasDBConnection) {
        this.categoriasDBConnection = categoriasDBConnection;
        this.categoriesDBConnection = categoriesDBConnection;
    }


    public boolean categoriesAlreadyInserted() {
        try {
            return categoriesDBConnection.categoriesAlreadyInserted();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getCategoriesName() {
        try {
            return categoriesDBConnection.getCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Categorias getOneCategory(int categoryID) {
        try {
            return categoriasDBConnection.getOneCategory(categoryID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getOneCategoryNameByID(int categoryID) {
        try {
            return categoriesDBConnection.getOneCategoryNameByID(categoryID);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void insertCategories(ArrayList<String> categories) {
        try {
            categoriesDBConnection.insertCategories(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCategoryID(String categoryName) {
        try {
            return categoriesDBConnection.getCategoryID(categoryName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
