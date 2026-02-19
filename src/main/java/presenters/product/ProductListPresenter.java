package presenters.product;

import presenters.StandardPresenter;
import utils.CategoryParser;
import utils.Product;
import utils.databases.ProductsDatabaseConnection;
import views.products.list.IProductListView;
import models.IProductListModel;
import views.products.IProductSearchView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ProductListPresenter extends StandardPresenter {
    private final IProductListView productListView;
    private final IProductListModel productListModel;
    private final ProductsDatabaseConnection productsDatabaseConnection;
    private static CategoryParser categoryParser;
    private static Logger LOGGER;

    public ProductListPresenter(IProductListView productListView, IProductListModel productListModel) {
        this.productListView = productListView;
        view = productListView;
        this.productListModel = productListModel;
        productsDatabaseConnection = new ProductsDatabaseConnection();
    }

    @Override
    protected void initListeners() {

    }

    public void onSearchViewOpenListButtonClicked() {
        ArrayList<Product> products = productListModel.getProductsFromDB();
        if(products.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay productos en la base de datos");
        }
        else {
            productListView.showView();
            productListView.setWorkingStatus();
            productListView.clearView();
            setProductsOnTable();
            productListView.setWaitingStatus();
        }
    }

    public void setProductsOnTable() {
        ArrayList<Product> products = productListModel.getProductsFromDB();
        int rowCount = 0;
        int productCategoryID;
        String productCategoryName = "";

            for (Product product : products) {
                try {
                    productCategoryID = productsDatabaseConnection.getCategoryID(product.getName());
                    productCategoryName = productsDatabaseConnection.getCategoryName(productCategoryID);
                } catch (Exception e) {
                    LOGGER.log(null,"ERROR IN METHOD 'setProductsOnTable' IN CLASS->'ProductsListPresenter'",e);

                }

                double clientPrice = product.calculateRealTimePrice().getValue0();
                double particularPrice = product.calculateRealTimePrice().getValue1();

                productListView.setStringTableValueAt(rowCount, 0, product.getName());
                productListView.setDoubleTableValueAt(rowCount, 1, clientPrice);
                productListView.setDoubleTableValueAt(rowCount, 2, particularPrice);
                productListView.setStringTableValueAt(rowCount, 3, CategoryParser.parseCategory(productCategoryName));
                rowCount++;
        }
    }
}
