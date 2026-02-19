package models;

import models.listeners.failed.CategoryCreationFailureListener;
import models.listeners.failed.CategorySearchFailureListener;
import models.listeners.successful.CategoryCreationSuccessListener;
import models.listeners.successful.CategorySearchSuccessListener;
import utils.databases.AttributesDatabaseConnection;
import utils.databases.CategoriesDatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryModel implements ICategoryModel {

    private final CategoriesDatabaseConnection categoriesDBConnection;

    public CategoryModel(CategoriesDatabaseConnection categoriesDBConnection) {
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
