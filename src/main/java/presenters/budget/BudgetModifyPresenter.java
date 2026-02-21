package presenters.budget;

import PdfFormater.IPdfConverter;
import PdfFormater.PdfConverter;
import PdfFormater.Row;
import utils.databases.hibernate.entities.Presupuestos;
import models.*;
import models.settings.ISettingsModel;
import presenters.StandardPresenter;
import utils.MessageTypes;
import utils.Product;
import utils.Client;
import utils.CategoryParser;
import views.budget.cuttingService.ICuttingServiceFormView;
import views.budget.modify.IBudgetModifyView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static utils.TextUtils.truncateAndRound;
import static utils.databases.SettingsTableNames.GENERAL;
import static utils.MessageTypes.*;

public class BudgetModifyPresenter extends StandardPresenter {

    // Record para estructurar los datos del producto de forma inmutable
    public record BudgetProduct(String name, int amount, String measures, String obs, double price) {
        public double getTotalLine() { return price * amount; }
    }

    private final IBudgetModifyView budgetModifyView;
    private final IBudgetModel budgetModel;
    private final IProductModel productModel;
    private final ICategoryModel categoryModel;
    private final IBudgetModifyModel budgetModifyModel;
    private final ISettingsModel settingsModel;
    private final ICuttingServiceFormView cuttingServiceFormView;

    private static final IPdfConverter pdfConverter = new PdfConverter();
    private static final Logger LOGGER = Logger.getLogger(BudgetModifyPresenter.class.getName());

    private int productsRowCountOnPreviewTable = -1;
    private int globalBudgetNumber = -1;
    private String oldClientName = "";
    private double globalBudgetTotalPrice = 0.0;
    private String globalClientType = "";

    public BudgetModifyPresenter(ICuttingServiceFormView cuttingServiceFormView, IBudgetModifyView budgetModifyView,
                                 IBudgetModel budgetModel, IProductModel productModel, ICategoryModel categoryModel,
                                 IBudgetModifyModel budgetModifyModel, ISettingsModel settingsModel) {
        this.budgetModifyView = budgetModifyView;
        this.view = budgetModifyView;
        this.budgetModel = budgetModel;
        this.productModel = productModel;
        this.categoryModel = categoryModel;
        this.budgetModifyModel = budgetModifyModel;
        this.settingsModel = settingsModel;
        this.cuttingServiceFormView = cuttingServiceFormView;
        cargarCiudades();
    }

    @Override
    protected void initListeners() { /* Implementar según necesidad de la vista */ }

    private void cargarCiudades() {
        budgetModifyView.setCitiesComboBox(budgetModel.getCitiesName());
    }

    // --- MÉTODOS DE BÚSQUEDA Y SELECCIÓN ---

    public void OnSearchProductButtonClicked() {
        String name = budgetModifyView.getProductsTextField().getText();
        List<Product> products = budgetModel.getProducts(name, "Seleccione una categoría");

        budgetModifyView.clearProductTable();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            String cat = CategoryParser.parseCategory(categoryModel.getOneCategoryNameByID(p.getCategoryID()));
            double price = p.calculateRealTimePrice().getValue0();

            budgetModifyView.setProductStringTableValueAt(i, 0, p.getName());
            budgetModifyView.setProductStringTableValueAt(i, 1, cat);
            budgetModifyView.setProductStringTableValueAt(i, 2, String.valueOf(price));
        }
    }

    public void increaseRowCountOnPreviewTable() {
        this.productsRowCountOnPreviewTable++;
    }

    public void onModifySearchViewButtonClicked(boolean filledRow, int selectedRow, int budgetNumber) {
        // 1. Validación temprana (Cláusula de guarda)
        if (selectedRow == -1 || !filledRow) {
            budgetModifyView.showMessage(BUDGET_MODIFY_FAILURE);
            return;
        }

        // 2. Preparación de la UI
        prepareViewForModification();

        // 3. Carga de estado global
        this.globalBudgetNumber = budgetNumber;
        this.oldClientName = budgetModifyModel.getOldClientName(budgetNumber);
        this.globalBudgetTotalPrice = 0.0;

        // 4. Obtención y seteo de datos
        loadBudgetDataIntoView(budgetNumber);

        // 5. Finalización
        this.productsRowCountOnPreviewTable = budgetModifyView.getFilledRowsCount(budgetModifyView.getPreviewTable());
        budgetModifyView.showView();
        budgetModifyView.setWaitingStatus();
    }

// Métodos de apoyo para mantener la función principal legible:

    private void prepareViewForModification() {
        budgetModifyView.setWorkingStatus();
        budgetModifyView.getWidthMeasureTextField().setEnabled(false);
        budgetModifyView.getHeightMeasureTextField().setEnabled(false);
        budgetModifyView.getClientSelectedCheckBox().setSelected(true);
    }

    private void loadBudgetDataIntoView(int budgetNumber) {
        ArrayList<String> budgetClientData = budgetModifyModel.getSelectedBudgetData(budgetNumber);
        String clientName = budgetClientData.get(1);

        // Agrupamos la carga en un solo bloque o método
        setModifyView(
                budgetModifyModel.getSavedProductNames(budgetNumber, clientName),
                budgetModifyModel.getSavedProductAmounts(budgetNumber, clientName),
                getProductObservations(budgetNumber, clientName),
                getProductsMeasures(budgetNumber, clientName),
                budgetNumber,
                getProductPrices(budgetNumber, clientName)
        );

        updateTextArea(true, true, globalBudgetTotalPrice);
    }

    public void setModifyView(ArrayList<String> names, ArrayList<Integer> amounts,
                              ArrayList<String> obs, ArrayList<String> measures,
                              int budgetNumber, ArrayList<Double> prices) {

        // 1. Obtener datos básicos del cliente desde el modelo
        ArrayList<String> budgetData = budgetModifyModel.getSelectedBudgetData(budgetNumber);
        String clientName = budgetData.get(1);
        String clientType = budgetData.get(3);

        // 2. Poblar la cabecera (Fila 0)
        SetClientInPreviewTable(clientName, clientType);

        // 3. Poblar los productos (Filas 1+)
        SetProductsInPreviewTable(prices, names, amounts, measures, obs);

        // 4. Calcular y establecer el precio total global
        this.globalBudgetTotalPrice = GetBudgetTotalPrice(amounts, prices);
    }

    private void SetClientInPreviewTable(String clientName, String clientType) {
        budgetModifyView.setPreviewStringTableValueAt(0, 0, clientName);
        budgetModifyView.setPreviewStringTableValueAt(0, 6, clientType);
        this.globalClientType = clientType;
    }

    private void SetProductsInPreviewTable(ArrayList<Double> prices, ArrayList<String> names,
                                   ArrayList<Integer> amounts, ArrayList<String> measures,
                                   ArrayList<String> obs) {
        for (int i = 0; i < names.size(); i++) {
            BudgetProduct bp = new BudgetProduct(names.get(i), amounts.get(i), measures.get(i), obs.get(i), prices.get(i));
            updateViewRow(i + 1, bp);
        }
    }

    private double GetBudgetTotalPrice(ArrayList<Integer> amounts, ArrayList<Double> prices) {
        double total = 0.0;
        for (int i = 0; i < amounts.size(); i++) {
            total += amounts.get(i) * prices.get(i);
        }
        return total;
    }

    private ArrayList<String> getProductObservations(int budgetNumber, String clientName) {
        return budgetModifyModel.getProductObservations(budgetNumber, clientName);
    }

    private ArrayList<String> getProductsMeasures(int budgetNumber, String clientName) {
        return budgetModifyModel.getProductMeasures(budgetNumber, clientName);
    }

    private ArrayList<Double> getProductPrices(int budgetNumber, String clientName) {
        return budgetModifyModel.getProductPrices(budgetNumber, clientName);
    }

    public Product GetSelectedProductFromProductsTable() {
        int row = budgetModifyView.getProductTableSelectedRow();
        if (row == -1) return null;

        String productName = budgetModifyView.getProductStringTableValueAt(row, 0);
        if (productName == null || productName.isEmpty()) return null;

        return productModel.getOneProduct(productModel.getProductID(productName));
    }

    // --- GESTIÓN DE LA TABLA DE PREVISUALIZACIÓN ---

    public void onAddProductButtonClicked() {
        Product product = GetSelectedProductFromProductsTable();
        if (product == null) {
            budgetModifyView.showMessage(PRODUCT_ADDING_FAILURE);
            return;
        }
        AddProductToPreviewTable(product, ++productsRowCountOnPreviewTable);
    }

    public void AddProductToPreviewTable(Product product, int row) {
        int amount = Integer.parseInt(defaultIfEmpty(budgetModifyView.getAmountTextField().getText(), "1"));
        double factor = calculateMeasureFactor();
        double unitPrice = product.calculateRealTimePrice().getValue0() * factor;

        BudgetProduct bp = new BudgetProduct(
                product.getName(),
                amount,
                getFormattedMeasures(),
                budgetModifyView.getObservationsTextField().getText(),
                unitPrice
        );

        updateViewRow(row, bp);
        updateTextArea(true, false, bp.getTotalLine());
    }

    private void updateViewRow(int row, BudgetProduct bp) {
        budgetModifyView.setPreviewStringTableValueAt(row, 1, bp.name());
        budgetModifyView.setPreviewStringTableValueAt(row, 2, String.valueOf(bp.amount()));
        budgetModifyView.setPreviewStringTableValueAt(row, 3, bp.measures());
        budgetModifyView.setPreviewStringTableValueAt(row, 4, bp.obs());
        budgetModifyView.setPreviewStringTableValueAt(row, 5, truncateAndRound(String.valueOf(bp.price())));
    }

    public void onDeleteProductButtonClicked() {
        JTable table = budgetModifyView.getPreviewTable();
        int row = table.getSelectedRow();

        if (row == -1 || productsRowCountOnPreviewTable < 1) {
            budgetModifyView.showMessage(PRODUCT_DELETION_FAILURE);
            return;
        }

        double lineTotal = Double.parseDouble(budgetModifyView.getPreviewStringTableValueAt(row, 5))
                * Integer.parseInt(budgetModifyView.getPreviewStringTableValueAt(row, 2));

        budgetModifyView.getPreviewTableModel().removeRow(row);
        productsRowCountOnPreviewTable--;
        updateTextArea(false, false, lineTotal);
    }

    // --- CÁLCULOS Y VALIDACIONES ---

    private double calculateMeasureFactor() {
        if (!CheckMeasureFieldsAreEnabled()) return 1.0;
        double h = Double.parseDouble(defaultIfEmpty(budgetModifyView.getHeightMeasureTextField().getText(), "1"));
        if (budgetModifyView.getWidthMeasureTextField().isEnabled()) {
            double w = Double.parseDouble(defaultIfEmpty(budgetModifyView.getWidthMeasureTextField().getText(), "1"));
            return h * w;
        }
        return h;
    }

    private String getFormattedMeasures() {
        if (!CheckMeasureFieldsAreEnabled()) return "-";
        String h = defaultIfEmpty(budgetModifyView.getHeightMeasureTextField().getText(), "1");
        if (budgetModifyView.getWidthMeasureTextField().isEnabled()) {
            String w = defaultIfEmpty(budgetModifyView.getWidthMeasureTextField().getText(), "1");
            return w + "m x " + h + "m";
        }
        return h + "m";
    }

    public boolean CheckMeasureFieldsAreEnabled() {
        return budgetModifyView.getWidthMeasureTextField().isEnabled() ||
                budgetModifyView.getHeightMeasureTextField().isEnabled();
    }

    public boolean onEmptyFields(int clientCol, int productCol) {
        String client = budgetModifyView.getPreviewStringTableValueAt(0, clientCol);
        String product = budgetModifyView.getPreviewStringTableValueAt(1, productCol);
        return (client == null || client.trim().isEmpty()) || (product == null || product.trim().isEmpty());
    }

    // --- PERSISTENCIA Y GUARDADO ---

    public void onSaveModificationsButtonClicked() {
        if (onEmptyFields(0, 1)) {
            budgetModifyView.showMessage(BUDGET_CREATION_EMPTY_COLUMN);
            return;
        }

        List<BudgetProduct> products = IntStream.rangeClosed(1, productsRowCountOnPreviewTable)
                .mapToObj(this::extractBudgetProductFromRow)
                .toList(); // Obtenemos la lista de productos directamente desde la tabla de previsualización, asumiendo que las filas están llenas y ordenadas.

        // Limpieza de presupuesto anterior
        int oldId = budgetModel.getBudgetID(globalBudgetNumber, oldClientName);
        Presupuestos oldBudget = budgetModel.findPresupuestoByID(oldId);
        budgetModel.deleteOneBudget(oldBudget);

        String clientName = budgetModifyView.getPreviewStringTableValueAt(0, 0);
        String clientType = budgetModifyView.getPreviewStringTableValueAt(0, 6);

        // Aplicar recargo si es particular antes de guardar
        double finalTotal = globalBudgetTotalPrice;
        if ("Particular".equals(clientType)) finalTotal *= 1.25;

        Presupuestos presupuesto = budgetModel.createBudget(clientName, budgetModifyView.getBudgetDate(), clientType, globalBudgetNumber, finalTotal);
        saveProductsToModel(presupuesto, products);

        // Generar PDF y cerrar
        Client client = budgetModel.GetOneClientByID(budgetModel.getClientID(clientName, clientType));
        GeneratePDF(client, products, globalBudgetNumber, finalTotal);

        budgetModifyView.showMessage(BUDGET_MODIFY_SUCCESS);
        closeAndRestart();
    }

    private BudgetProduct extractBudgetProductFromRow(int row) {
        return new BudgetProduct(
                budgetModifyView.getPreviewStringTableValueAt(row, 1),
                Integer.parseInt(budgetModifyView.getPreviewStringTableValueAt(row, 2)),
                budgetModifyView.getPreviewStringTableValueAt(row, 3),
                budgetModifyView.getPreviewStringTableValueAt(row, 4),
                Double.parseDouble(budgetModifyView.getPreviewStringTableValueAt(row, 5))
        );
    }

    private void saveProductsToModel(Presupuestos presupuesto, List<BudgetProduct> products) {
        budgetModel.saveProducts(
                presupuesto,
                products.stream().map(BudgetProduct::amount).toList(),
                products.stream().map(BudgetProduct::name).toList(),
                products.stream().map(BudgetProduct::obs).toList(),
                products.stream().map(BudgetProduct::measures).toList(),
                products.stream().map(BudgetProduct::price).toList()
        );
    }

    private void GeneratePDF(Client client, List<BudgetProduct> products, int num, double total) {
        ArrayList<Row> pdfRows = new ArrayList<>();
        products.forEach(p -> pdfRows.add(new Row(p.name(), p.amount(), p.measures(), p.obs(), p.price(), p.getTotalLine())));
        try {
            pdfConverter.generateBill(false, client, num, pdfRows, total);
        } catch (Exception e) {
            LOGGER.severe("Error al generar PDF: " + e.getMessage());
        }
    }

    // --- ACTUALIZACIÓN DE UI ---

    public void updateTextArea(boolean adding, boolean isInit, double price) {
        if (!isInit) {
            globalBudgetTotalPrice += adding ? price : -price;
        }
        budgetModifyView.getPriceTextArea().setText("Precio total: $" + truncateAndRound(String.valueOf(globalBudgetTotalPrice)));
    }

    private String defaultIfEmpty(String val, String def) {
        return (val == null || val.trim().isEmpty()) ? def : val;
    }

    private void closeAndRestart() {
        budgetModifyView.hideView();
        budgetModifyView.getWindowFrame().dispose();
        budgetModifyView.restartWindow();
    }

    // --- GESTIÓN DE CLIENTES ---

    /**
     * Busca clientes basados en el nombre y la ciudad seleccionada en la vista.
     */
    public void OnSearchClientButtonClicked() {
        String city = (String) budgetModifyView.getCitiesComboBox().getSelectedItem();
        // Si no hay ciudad seleccionada, enviamos string vacío para buscar en todas
        if ("Seleccione una ciudad".equals(city)) city = "";

        String name = budgetModifyView.getBudgetClientName();
        ArrayList<Client> clients = budgetModel.getClients(name, city);

        budgetModifyView.clearClientTable();

        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            String clientType = client.isClient() ? "Cliente" : "Particular";
            // Obtenemos el ID real de la base de datos
            int clientID = budgetModel.getClientID(client.getName(), clientType);

            budgetModifyView.setClientIntTableValueAt(i, 0, clientID);
            budgetModifyView.setClientStringTableValueAt(i, 1, client.getName());
            budgetModifyView.setClientStringTableValueAt(i, 2, client.getAddress());
            budgetModifyView.setClientStringTableValueAt(i, 3, client.getCity());
            budgetModifyView.setClientStringTableValueAt(i, 4, client.getPhone());
            budgetModifyView.setClientStringTableValueAt(i, 5, clientType);
        }
    }

    /**
     * Agrega el cliente seleccionado de la tabla de búsqueda a la tabla de previsualización.
     */
    public void onAddClientButtonClicked() {
        int selectedRow = budgetModifyView.getClientTableSelectedRow();

        if (selectedRow == -1) {
            budgetModifyView.showMessage(MessageTypes.CLIENT_NOT_SELECTED);
            return;
        }

        Object nameObj = budgetModifyView.getClientResultTableModel().getValueAt(selectedRow, 1);
        Object typeObj = budgetModifyView.getClientResultTableModel().getValueAt(selectedRow, 5);

        if (nameObj == null || nameObj.toString().isEmpty()) {
            budgetModifyView.showMessage(MessageTypes.CLIENT_NOT_SELECTED);
            return;
        }

        String clientName = nameObj.toString();
        String clientType = typeObj.toString();

        // Actualizamos la fila 0 (Cabecera del cliente) en la tabla de previsualización
        budgetModifyView.setPreviewStringTableValueAt(0, 0, clientName);
        budgetModifyView.setPreviewStringTableValueAt(0, 6, clientType);

        this.globalClientType = clientType;
        budgetModifyView.getClientSelectedCheckBox().setSelected(true);

        // Si el cliente cambia, recalculamos precios por posibles recargos
        updatePriceColumnByRecharge();
    }

    /**
     * Actualiza la visibilidad de los paneles según si hay un cliente seleccionado o no.
     */
    public void onClientSelectedCheckBoxClicked() {
        if (!budgetModifyView.getClientSelectedCheckBox().isSelected()) {
            budgetModifyView.setSecondPanelsVisibility();
        } else {
            budgetModifyView.setInitialPanelsVisibility();
        }
    }

    /**
     * Recalcula los precios de la tabla de previsualización si el tipo de cliente cambia.
     */
    public void updatePriceColumnByRecharge() {
        double recharge = 1.0;
        String clientType = budgetModifyView.getPreviewStringTableValueAt(0, 6);

        // Limpiamos el total actual para recalcular desde cero
        updateTextArea(false, false, globalBudgetTotalPrice);
        globalBudgetTotalPrice = 0;

        if ("Particular".equals(clientType)) {
            recharge = Double.parseDouble(settingsModel.getModularValue(GENERAL, "Recargo por particular"));
        }

        for (int i = 1; i <= productsRowCountOnPreviewTable; i++) {
            String prodName = budgetModifyView.getPreviewStringTableValueAt(i, 1);
            Product product = productModel.getOneProduct(productModel.getProductID(prodName));

            double originalPrice = product.calculateRealTimePrice().getValue0();
            double priceWithRecharge = originalPrice * recharge;
            int amount = Integer.parseInt(budgetModifyView.getPreviewStringTableValueAt(i, 2));

            budgetModifyView.setPreviewStringTableValueAt(i, 5, String.valueOf(priceWithRecharge));
            updateTextArea(true, false, priceWithRecharge * amount);
        }
    }

    // --- SERVICIOS EXTRA ---

    public void OnAddCuttingServiceButtonClicked() {
        cuttingServiceFormView.showView();
        cuttingServiceFormView.setCreateMode(false);
    }
}