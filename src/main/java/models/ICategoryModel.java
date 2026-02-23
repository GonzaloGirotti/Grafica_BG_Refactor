package models;

import models.listeners.failed.CategoryCreationFailureListener;
import models.listeners.failed.CategorySearchFailureListener;
import models.listeners.successful.CategoryCreationSuccessListener;
import models.listeners.successful.CategorySearchSuccessListener;
import utils.databases.hibernate.entities.Categorias;

import java.util.ArrayList;
import java.util.List;


public interface ICategoryModel {
    List<String> getCategoriesName();
    void insertCategories(ArrayList<String> categoriesNames);
    int getCategoryID(String categoryName);
    boolean categoriesAlreadyInserted();
    String getOneCategoryNameByID(int categoryID);
    Categorias getOneCategory(int categoryID);
}
