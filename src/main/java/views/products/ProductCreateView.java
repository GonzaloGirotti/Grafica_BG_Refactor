package views.products;

import lombok.Getter;
import models.CategoryModel;
import presenters.StandardPresenter;
import presenters.product.ProductCreatePresenter;
import presenters.product.ProductPresenter;
import utils.GetCategoryPanelsMap;
import utils.databases.CategoriesDatabaseConnection;
import utils.databases.ProductsDatabaseConnection;
import views.ToggleableView;
import views.products.modular.IModularCategoryView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

import static utils.CategoryParser.parseCategory;
import static utils.WindowFormatter.relativeSizeAndCenter;

public class ProductCreateView extends ToggleableView implements IProductCreateView {
    @Getter
    private JPanel containerPanel;
    private JPanel productSpecsContainer;
    private JTextField productNameField;
    private JLabel productLabel;
    @Getter
    private JComboBox<String> categoryComboBox;
    private JLabel categoryLabel;
    private JTextField productPriceField;
    private JPanel createButtonContainer;
    private JButton createButton;
    private JPanel modularContainer;
    private JButton updatePriceButton;
    private JCheckBox priceEditCheckBox;
    private JButton savePricesButton;
    private JComboBox<String> subCategoryComboBox;
    private JPanel comboBoxOriginalPanel;
    private ProductCreatePresenter productCreatePresenter;
    private ProductPresenter productPresenter;
    private CategoryModel categoryModel;
    @Getter
    private IModularCategoryView modularView;
    private final CategoriesDatabaseConnection categoriesDatabaseConnection = new CategoriesDatabaseConnection();
    private final ProductsDatabaseConnection productDatabaseConnection = new ProductsDatabaseConnection();
    private final GetCategoryPanelsMap getCategoryPanelsMap = new GetCategoryPanelsMap();
    private final Map<String, IModularCategoryView> modularMap;
    private int lastProductCreatedID = -1;

    public ProductCreateView() {
        windowFrame = new JFrame("Crear Producto");
        windowFrame.setContentPane(containerPanel);
        windowFrame.pack();
        windowFrame.setLocationRelativeTo(null);
        windowFrame.setIconImage(new ImageIcon("src/main/resources/BGLogo.png").getImage());

        relativeSizeAndCenter(windowFrame, 0.95, 0.7);

        cambiarTamanioFuente(containerPanel, 14);
        windowFrame.setResizable(true);
        modularContainer.setLayout(new BorderLayout());
        modularMap = getCategoryPanelsMap.getCategoryPanelsMap(productCreatePresenter);
    }


    public void showSelectedView(String category) {
        // Limpiar el panel del contenedor
        modularContainer.removeAll();

        // Mostrar la vista correspondiente
        IModularCategoryView selectedView = getCorrespondingModularView(category);
        if (selectedView != null && selectedView.getContainerPanel() != null) {
            modularView = selectedView;
            modularContainer.add(selectedView.getContainerPanel(), BorderLayout.CENTER);
            selectedView.getContainerPanel().setVisible(true);
        }

        // Actualizar el layout del panel
        modularContainer.revalidate();
        modularContainer.repaint();
    }


    public IModularCategoryView getCorrespondingModularView(String category) {
        IModularCategoryView correspondingModularView = null;
        Map<String, IModularCategoryView> panelesCategorias = getCategoryPanelsMap.getCategoryPanelsMap(productCreatePresenter);

        for (String categoria : panelesCategorias.keySet()) {

            if (parseCategory(categoria).equals(category)) {
                correspondingModularView = panelesCategorias.get(categoria);
                break;
            }
        }
        return correspondingModularView;
    }

    @Override
    public String getProductName() {
        return productNameField.getText();
    }

    @Override
    public String getProductCategory() {
        return (String) categoryComboBox.getSelectedItem();
    }

    @Override
    public String getProductCategoryEnglish() {
        String s = (String) categoryComboBox.getSelectedItem();
        assert s != null; // Esta línea evita el error de posible null. Hace que el programa falle si s es null, lo cual no debería suceder si el combo box está correctamente configurado.
        return switch (s) {
            case "Taza" -> "Cup";
            case "Gorra" -> "Cap";
            case "Prenda" -> "Clothes";
            case "Tela" -> "Cloth";
            case "Bandera" -> "Flag";
            case "Servicios comunes" -> "CommonServices";
            case "Servicio de corte" -> "CuttingService";
            case "Impresión lineal" -> "LinearPrinting";
            case "Impresión en metro cuadrado" -> "SquareMeterPrinting";
            default -> s;
        };
    }

    @Override
    public void clearView() {
        productNameField.setText("");
        categoryComboBox.setSelectedIndex(0);
        modularContainer.removeAll();
    }

    @Override
    protected void wrapContainer() {
        containerPanelWrapper = containerPanel;
    }

    @Override
    protected void initListeners() {
        createButton.addActionListener(e -> {
            lastProductCreatedID = productCreatePresenter.onCreateButtonClicked();
            if (lastProductCreatedID != -1) {
                clearView();
            }
        });
        productNameField.addActionListener(l -> {
            lastProductCreatedID = productCreatePresenter.onCreateButtonClicked();
            if (lastProductCreatedID != -1) {
                clearView();
            }
        });
    }


    @Override
    public void setPresenter(StandardPresenter productCreatePresenter) {
        this.productCreatePresenter = (ProductCreatePresenter) productCreatePresenter;
    }

    public void setCategorias(List<String> categorias) {
        categoryComboBox.addItem("Seleccione una categoría");
        for (String categoria : categorias) {
            categoryComboBox.addItem(categoria);
        }
    }

    @Override
    public void comboBoxListenerSet(ItemListener listener) {
        categoryComboBox.addItemListener(listener);
    }

}