package views.products;


import lombok.Getter;
import presenters.StandardPresenter;
import presenters.product.ProductListPresenter;
import presenters.product.ProductPresenter;
import presenters.product.ProductSearchPresenter;
import utils.CategoryParser;
import utils.GetCategoryPanelsMap;
import utils.Product;
import utils.TextUtils;
import views.ToggleableView;
import views.products.modular.IModularCategoryView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.CategoryParser.parseCategory;
import static utils.WindowFormatter.relativeSizeAndCenter;

public class ProductSearchView extends ToggleableView implements IProductSearchView {

    private JPanel containerPanel;
    private JPanel productSearchContainer;
    private JPanel searchFieldContainer;
    private JPanel searchButtonsContainer;
    private JPanel searchResultContainer;
    private JPanel productListButtonsContainer;
    private JTextField searchField;
    private JButton searchButton;
    private JScrollPane productResultScrollPanel;
    @Getter
    private JTable productResultTable;
    private JButton productListOpenButton;
    private JButton deleteOneProductButton;
    private JPanel deleteButtonsContainer;
    private JPanel bottomButtonsContainer;
    private JComboBox categoryComboBox;
    private JLabel categoryLabel;
    private final TextUtils textUtils = new TextUtils();
    private JPanel modularContainer;
    private JButton modifyProductButton;
    private JPanel modifyProductButtonContainer;
    @Getter
    private JTextField newProductNameTextField;
    private JButton deleteAllProductsButton;
    private ProductSearchPresenter productSearchPresenter;
    private ProductPresenter productPresenter;
    private DefaultTableModel tableModel;
    private final ProductListPresenter productListPresenter;
    @Getter
    private IModularCategoryView modularView;
    private final GetCategoryPanelsMap getCategoryPanelsMap = new GetCategoryPanelsMap();

    public ProductSearchView(ProductListPresenter productListPresenter) {
        windowFrame = new JFrame("Buscar Productos");
        windowFrame.setContentPane(containerPanel);
        windowFrame.pack();
        windowFrame.setLocationRelativeTo(null);
        windowFrame.setIconImage(new ImageIcon("src/main/resources/BGLogo.png").getImage());

        this.productListPresenter = productListPresenter;
        cambiarTamanioFuente(containerPanel, 14);
        relativeSizeAndCenter(windowFrame, 0.9, 0.9);
        windowFrame.setResizable(false);
    }

    @Override
    public void start() {
        super.start();
        tableModel = new DefaultTableModel(new Object[]{"Nombre", "Categoría", "Precio Cliente", "Precio Particular"}, 200) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productResultTable.setModel(tableModel);
    }

    @Override
    public void setPresenter(StandardPresenter productSearchPresenter) {
        this.productSearchPresenter = (ProductSearchPresenter) productSearchPresenter;
    }

    @Override
    protected void wrapContainer() {
        containerPanelWrapper = containerPanel;
    }

    @Override
    protected void initListeners() {
        searchButton.addActionListener(e -> productSearchPresenter.onSearchButtonClicked());
        deleteOneProductButton.addActionListener(e -> productSearchPresenter.onDeleteProductButtonClicked());
        productListOpenButton.addActionListener(e -> productListPresenter.onSearchViewOpenListButtonClicked());
        modifyProductButton.addActionListener(e -> productSearchPresenter.onModifyButtonPressed());
        searchField.addActionListener(e -> {
            productSearchPresenter.onSearchButtonClicked();
        });
    }

    public void setTableListener(ListSelectionListener listener) {
        productResultTable.getSelectionModel().addListSelectionListener(listener);
        productResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Add listener to all rows. When a row is selected, the listener will trigger the method "ChargeProductNameOnTextField".
        productResultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (getSelectedTableRow() != -1) {
                    ChargeProductNameOnTextField();
                }
            }
        });
    }

    private void setModularPrices() {
        if (modularView != null) {
            modularView.setPriceTextFields();
        }
    }

    @Override
    public void showSelectedView(String category, Product product) {
        // Limpiar el panel del contenedor
        modularContainer.removeAll();
        appearModularView();

        // Mostrar la vista correspondiente
        // JPanel selectedView = getCorrespondingModularView(category);
        IModularCategoryView selectedView = getCorrespondingModularView(category);
        if (selectedView != null && selectedView.getContainerPanel() != null) {
            modularView = selectedView;
            modularContainer.setLayout(new BorderLayout());
            modularContainer.add(selectedView.getContainerPanel(), BorderLayout.CENTER);
            selectedView.getContainerPanel().setSize(1000, 200);
            selectedView.getContainerPanel().setVisible(true);
            modularView.loadComboBoxValues();
            modularView.setSearchTextFields(product);
            modularView.calculateDependantPrices();
        }

        // Actualizar el layout del panel
        modularContainer.revalidate();
        modularContainer.repaint();
    }

    public void clearModularView() {
        modularContainer.removeAll();
        modularContainer.revalidate();
        modularContainer.repaint();
        newProductNameTextField.setText("");
        modularView = null;
    }

    public IModularCategoryView getCorrespondingModularView(String category) {
        IModularCategoryView correspondingModularView = null;
        Map<String, IModularCategoryView> panelesCategorias = getCategoryPanelsMap.getCategoryPanelsMap(productSearchPresenter);

        for (String categoria : panelesCategorias.keySet()) {

            if (parseCategory(categoria).equals(category)) {
                correspondingModularView = panelesCategorias.get(categoria);
                break;
            }
        }
        return correspondingModularView;
    }

    public void ChargeProductNameOnTextField() {
        Object selectedProductName = productResultTable.getValueAt(getSelectedTableRow(), 0);
        newProductNameTextField.setText((String) selectedProductName);
    }

    @Override
    public void clearView() {
        for (int row = 0; row < productResultTable.getRowCount(); row++) {
            for (int col = 0; col < productResultTable.getColumnCount(); col++) {
                productResultTable.setValueAt("", row, col);
            }
        }
        productResultTable.clearSelection();
        modularContainer.removeAll();
    }

    @Override
    public String getNameSearchText() {
        return searchField.getText();
    }

    public void setStringTableValueAt(int row, int col, String value) {
        productResultTable.setValueAt(value, row, col);
    }

    public void setDoubleTableValueAt(int row, int col, double value) {
        productResultTable.setValueAt(value, row, col);
    }

    @Override
    public void setIntTableValueAt(int row, int col, int value) {
        productResultTable.setValueAt(value, row, col);
    }

    public int getSelectedTableRow() {
        return productResultTable.getSelectedRow();
    }

    public void deselectAllRows() {
        productResultTable.clearSelection();
    }

    @Override
    public void setCategoriesComboBox(List<String> categorias) {
        categoryComboBox.addItem("Seleccione una categoría");
        for (String categoria : categorias) {
            categoryComboBox.addItem(CategoryParser.parseCategory(categoria));
        }
    }

    @Override
    public JComboBox getCategoriesComboBox() {
        return categoryComboBox;
    }

    public void hideModularView() {
        modularContainer.setVisible(false);
    }

    public void appearModularView() {
        modularContainer.setVisible(true);
    }

}
