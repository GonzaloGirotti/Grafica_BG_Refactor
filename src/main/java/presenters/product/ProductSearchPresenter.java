package presenters.product;

import models.ICategoryModel;
import models.IProductModel;
import models.settings.ISettingsModel;
import models.settings.SettingsModel;
import org.javatuples.Pair;
import utils.Attribute;
import utils.CategoryParser;
import utils.databases.SettingsTableNames;
import views.products.IProductSearchView;
import utils.Product;
import views.products.modular.IModularCategoryView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.MessageTypes.PRODUCT_DELETION_FAILURE;
import static utils.MessageTypes.PRODUCT_SEARCH_FAILURE;

public class ProductSearchPresenter extends ProductPresenter {
    private final IProductSearchView productSearchView;
    private final IProductModel productModel;
    private final ICategoryModel categoryModel;
    private final ISettingsModel settingsModel;
    private IModularCategoryView modularView;

    public ProductSearchPresenter(ISettingsModel settingsModel, IProductSearchView productSearchView, IProductModel productModel, ICategoryModel categoryModel) {
        super((SettingsModel) settingsModel);
        this.productSearchView = productSearchView;
        view = productSearchView;
        this.productModel = productModel;
        this.categoryModel = categoryModel;
        this.settingsModel = settingsModel;

        this.productSearchView.setTableListener(this::handleTableSelection);
        cargarCategorias();
    }

    private void handleTableSelection(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = productSearchView.getProductResultTable().getSelectedRow();
            if (selectedRow != -1) {
                String selectedCategory = (String) productSearchView.getProductResultTable().getValueAt(selectedRow, 1);
                String selectedProductName = (String) productSearchView.getProductResultTable().getValueAt(selectedRow, 0);
                Product selectedProduct = productModel.getOneProduct(productModel.getProductID(selectedProductName));
                productSearchView.showSelectedView(selectedCategory, selectedProduct);
                modularView = productSearchView.getModularView();
            }
        }
    }

    @Override
    protected void initListeners() {
        productModel.addProductSearchSuccessListener(() -> {
            ArrayList<Product> products = productModel.getLastProductsQuery();
            int rowCount = 0;
            for (Product product : products) {
                String categoryName = productModel.getCategoryName(product.getCategoryID());
                Pair<Double, Double> prices = product.calculateRealTimePrice();
                double clientPrice = prices.getValue0();
                double particularPrice = prices.getValue1();
                productSearchView.setStringTableValueAt(rowCount, 0, product.getName());
                productSearchView.setStringTableValueAt(rowCount, 1, CategoryParser.parseCategory(categoryName));
                productSearchView.setDoubleTableValueAt(rowCount, 2, clientPrice);
                productSearchView.setDoubleTableValueAt(rowCount, 3, particularPrice);
                rowCount++;
            }
        });

        productModel.addProductSearchFailureListener(() -> productSearchView.showMessage(PRODUCT_SEARCH_FAILURE));
    }

    public void onSearchButtonClicked() {
        productSearchView.clearView();
        String productName = productSearchView.getNameSearchText();
        JComboBox<String> categoryComboBox = productSearchView.getCategoriesComboBox();
        Object selectedItem = categoryComboBox.getSelectedItem();
        String selectedCategory = selectedItem != null ? CategoryParser.getProductCategoryEnglish((String) selectedItem) : "";
        productModel.queryProducts(productName, selectedCategory);
    }

    public void onHomeSearchProductButtonClicked() {
        productSearchView.showView();
    }

    public void onDeleteProductButtonClicked() {
        int[] selectedRows = productSearchView.getProductResultTable().getSelectedRows();
        if (selectedRows.length == 1) {
            deleteOneProduct();
        } else {
            productSearchView.showMessage(PRODUCT_DELETION_FAILURE);
        }
    }

    public void deleteOneProduct() {
        int selectedRow = productSearchView.getProductResultTable().getSelectedRow();
        int productID;
        Object rowValue;

        if (selectedRow != -1) {
            rowValue = productSearchView.getProductResultTable().getValueAt(selectedRow, 0);
            if (rowValue != null && !rowValue.equals("")) {
                String selectedProductName = (String) rowValue;
                productID = productModel.getProductID(selectedProductName);
                productModel.deleteOneProduct(productID, false);
                productSearchView.setWorkingStatus();
                productSearchView.clearView();
                String searchedName = productSearchView.getNameSearchText();
                productModel.queryProducts(searchedName, "Seleccione una categoría");
                productSearchView.deselectAllRows();
                productSearchView.clearModularView();
                productSearchView.setWaitingStatus();
            } else {
                productSearchView.showMessage(PRODUCT_DELETION_FAILURE);
            }
        } else {
            productSearchView.showMessage(PRODUCT_DELETION_FAILURE);
        }
    }

    public void cargarCategorias() {
        List<String> categorias = categoryModel.getCategoriesName();
        productSearchView.setCategoriesComboBox(categorias);
    }

    public double getIndividualPrice(SettingsTableNames tableName, String selectedValue) {
        return Double.parseDouble(settingsModel.getModularValue(tableName, selectedValue));
    }

    public Map<String, String> getProductAttributes(Product product) {
        int productID = productModel.getProductID(product.getName());
        return productModel.getProductAttributes(productID);
    }

    public void onModifyButtonPressed() {
        int selectedRow = productSearchView.getProductResultTable().getSelectedRow();
        ArrayList<Attribute> attributes = new ArrayList<>();
        JTextField newProductNameTextField = productSearchView.getNewProductNameTextField();

        if (selectedRow != -1) {
            String selectedCategory = (String) productSearchView.getProductResultTable().getValueAt(selectedRow, 1);
            String englishCategory = CategoryParser.getProductCategoryEnglish(selectedCategory);

            String newProductName = newProductNameTextField.getText();
            if (newProductName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor ingrese un nombre para el producto!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedProductName = (String) productSearchView.getProductResultTable().getValueAt(selectedRow, 0);
            int categoryID = categoryModel.getCategoryID(englishCategory);
            attributes = modularView.getAttributes();

            productModel.deleteOneProduct(productModel.getProductID(selectedProductName), true);
            productModel.createProduct(selectedProductName, categoryID, true);
            int newproductID = productModel.getProductID(selectedProductName);

            updateProductName(newproductID, newProductName);
            productModel.instantiateProductAttributes(newproductID, attributes, categoryID);

            JOptionPane.showMessageDialog(null, "Producto modificado con éxito!", "Success", JOptionPane.INFORMATION_MESSAGE);

            productSearchView.hideModularView();
            productSearchView.clearModularView();
            onSearchButtonClicked();
        }
    }

    public void updateProductName(int productID, String newName) {
        Product product = productModel.getOneProduct(productID);
        if (product != null) {
            productModel.updateProductName(productID, newName);
        }
    }
}
