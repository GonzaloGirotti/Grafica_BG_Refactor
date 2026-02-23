package models;

import models.listeners.failed.ProductCreationFailureListener;
import models.listeners.failed.ProductSearchFailureListener;
import models.listeners.successful.ProductCreationSuccessListener;
import models.listeners.successful.ProductSearchSuccessListener;
import utils.Attribute;
import utils.Product;
import utils.databases.AttributesDatabaseConnection;
import utils.databases.CategoriesDatabaseConnection;
import utils.databases.ProductsDatabaseConnection;
import utils.databases.hibernate.ProductosDBConnection;
import utils.databases.hibernate.entities.Categorias;
import utils.databases.hibernate.entities.Productos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProductModel implements IProductModel {

    private final AttributesDatabaseConnection attributesDBConnection;
    private final ProductsDatabaseConnection productsDBConnection;
    private final ProductosDBConnection productosDBConnection;
    private final CategoriesDatabaseConnection categoriesDBConnection;
    private final List<ProductCreationSuccessListener> productCreationSuccessListeners;
    private final List<ProductCreationFailureListener> productCreationFailureListeners;
    private final List<ProductSearchSuccessListener> productSearchSuccessListeners;
    private final List<ProductSearchFailureListener> productSearchFailureListeners;
    private ArrayList<Product> products;

    public ProductModel(ProductsDatabaseConnection dbConnection,
                        ProductosDBConnection productosDBConnection,
                        AttributesDatabaseConnection attributesDBConnection,
                        CategoriesDatabaseConnection categoriesDBConnection) {

        this.productsDBConnection = dbConnection;
        this.productosDBConnection = productosDBConnection;
        this.attributesDBConnection = attributesDBConnection;
        this.categoriesDBConnection = categoriesDBConnection;
		products = new ArrayList<>();

        this.productCreationSuccessListeners = new LinkedList<>();
        this.productCreationFailureListeners = new LinkedList<>();

        this.productSearchSuccessListeners = new LinkedList<>();
        this.productSearchFailureListeners = new LinkedList<>();
    }

    public void updateProductName(int productID, String newName) {
        try {
            productsDBConnection.updateProductName(productID, newName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int createProduct(String productName, Categorias categoria, boolean isModify) {
        try {
            Productos producto = new Productos();
            producto.setNombre(productName);
            producto.setCategoria(categoria);
            productosDBConnection.saveProducto(producto);
            int productID = productsDBConnection.getProductID(productName);

            if(!isModify){
                notifyProductCreationSuccess();
            }

            return productID;
        } catch (Exception e) {
            notifyProductCreationFailure();
        }
        return -1;
    }

    @Override
    public void instantiateProductAttributes(int productID, ArrayList<Attribute> attributes, int categoryID) {
        for (Attribute attribute : attributes) {
			attributesDBConnection.insertAttributeRow(attribute.getName(), productID, attribute.getValue());
		}
    }

    public int getCategoryID(String productCategory) {
        try {
            return productsDBConnection.getCategoryID(productCategory);
        } catch (SQLException e) {
            return -1;
        }
    }

    public String getCategoryName(int categoryID) {
        return categoriesDBConnection.getCategoryName(categoryID);
    }

    @Override
    public ArrayList<Product> getLastProductsQuery() {
        return products;
    }

    @Override
    public int getProductID(String productName) {
        int productID = 0;
        try {
            productID = productsDBConnection.getProductID(productName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  productID;
    }

    @Override
    public void deleteOneProduct(int oneProductID, boolean isModify) {
        try {
            productsDBConnection.deleteOneProductFromDB(oneProductID, isModify);
            attributesDBConnection.deleteProductAttributesByID(oneProductID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProductCreationSuccessListener(ProductCreationSuccessListener listener) {
        productCreationSuccessListeners.add(listener);
    }

    @Override
    public void addProductCreationFailureListener(ProductCreationFailureListener listener) {
        productCreationFailureListeners.add(listener);
    }

    @Override
    public void addProductSearchSuccessListener(ProductSearchSuccessListener listener) {
        productSearchSuccessListeners.add(listener);
    }

    @Override
    public void addProductSearchFailureListener(ProductSearchFailureListener listener) {
        productSearchFailureListeners.add(listener);
    }

    @Override
    public void queryProducts(String searchedName, String category) {
        try {
            products = productsDBConnection.getProducts(searchedName, category);
            notifyProductSearchSuccess();
        } catch (SQLException e) {
            notifyProductSearchFailure();
        }
    }

    private void notifyProductCreationSuccess() {
        for (ProductCreationSuccessListener listener : productCreationSuccessListeners) {
            listener.onSuccess();
        }
    }

    private void notifyProductCreationFailure() {
        for (ProductCreationFailureListener listener : productCreationFailureListeners) {
            listener.onFailure();
        }
    }

    private void notifyProductSearchSuccess() {
        for (ProductSearchSuccessListener listener : productSearchSuccessListeners) {
            listener.onSuccess();
        }
    }

    private void notifyProductSearchFailure() {
        for (ProductSearchFailureListener listener : productSearchFailureListeners) {
            listener.onFailure();
        }
    }

    public Product getOneProduct(int productID) {
        try {
            return productsDBConnection.getOneProduct(productID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getProductAttributes(int productID) {
        try {
            return attributesDBConnection.getProductAttributes(productID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void DeleteProductAttributes(int productID) {
        try {
            attributesDBConnection.deleteProductAttributesByID(productID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
