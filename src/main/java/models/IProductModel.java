package models;

import models.listeners.failed.ProductCreationFailureListener;
import models.listeners.failed.ProductSearchFailureListener;
import models.listeners.successful.ProductCreationSuccessListener;
import models.listeners.successful.ProductSearchSuccessListener;
import utils.Attribute;
import utils.Product;

import java.util.ArrayList;
import java.util.Map;

public interface IProductModel {
    int createProduct(String productName, int categoryID, boolean isModify);

    void addProductCreationSuccessListener(ProductCreationSuccessListener listener);
    void addProductCreationFailureListener(ProductCreationFailureListener listener);
    void addProductSearchSuccessListener(ProductSearchSuccessListener listener);
    void addProductSearchFailureListener(ProductSearchFailureListener listener);
    void queryProducts(String searchedName, String category);
    ArrayList<Product> getLastProductsQuery();
    int getProductID(String productName);
    void deleteOneProduct(int productID, boolean isModify);
    String getCategoryName(int categoryID);
    void instantiateProductAttributes(int productID, ArrayList<Attribute> attributes, int categoryID);
    int getCategoryID(String categoryName);
    Product getOneProduct(int productID);
    Map<String, String> getProductAttributes(int productID);
    void updateProductName(int productID, String newName);
}
