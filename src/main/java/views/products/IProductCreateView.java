package views.products;

import views.IToggleableView;
import views.products.modular.IModularCategoryView;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.util.List;
public interface IProductCreateView extends IToggleableView {
    String getProductName();
    String getProductCategory();
    String getProductCategoryEnglish();
    void setCategorias(List<String> categorias);
    JPanel getContainerPanel();
    void comboBoxListenerSet(ItemListener listener);
    IModularCategoryView getModularView();
    IModularCategoryView getCorrespondingModularView(String category);
    void showSelectedView(String category);
	void clearView();
}
