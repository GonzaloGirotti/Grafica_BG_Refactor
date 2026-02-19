package views.products;

import utils.Product;
import views.IToggleableView;
import views.products.modular.IModularCategoryView;

import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public interface IProductSearchView extends IToggleableView {
    String getNameSearchText();
    void setStringTableValueAt(int row, int col, String value);
    void setDoubleTableValueAt(int row, int col, double value);
    void setIntTableValueAt(int row, int col, int value);
    void clearView();
    int getSelectedTableRow();
    void deselectAllRows();
    JTable getProductResultTable();
    void setCategoriesComboBox(List<String> categorias);
    JComboBox getCategoriesComboBox();
	void setTableListener(ListSelectionListener listener);
    void showSelectedView(String selectedCategory, Product product);
    IModularCategoryView getModularView();
    void hideModularView();
    void appearModularView();
    JTextField getNewProductNameTextField();
    void clearModularView();
}
