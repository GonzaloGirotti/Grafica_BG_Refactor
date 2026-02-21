package main;

import com.formdev.flatlaf.FlatLightLaf;
import models.*;
import models.settings.ISettingsModel;
import models.settings.SettingsModel;
import presenters.budget.BudgetCreatePresenter;
import presenters.budget.BudgetListPresenter;
import presenters.budget.BudgetModifyPresenter;
import presenters.budget.BudgetSearchPresenter;
import presenters.categories.CategoryCreatePresenter;
import presenters.client.BudgetHistoryPresenter;
import presenters.client.ClientCreatePresenter;
import presenters.client.ClientListPresenter;
import presenters.client.ClientSearchPresenter;
import presenters.factory.IPresenterFactory;
import presenters.factory.PresenterFactoryImpl;
import presenters.product.ProductCreatePresenter;
import presenters.product.ProductListPresenter;
import presenters.budget.CuttingServiceFormPresenter;
import presenters.product.ProductSearchPresenter;
import presenters.settings.SettingsPresenter;
import presenters.workbudget.WorkBudgetCreatePresenter;
import presenters.workbudget.WorkBudgetSearchPresenter;
import testing.entities.Presupuestos;
import utils.databases.*;
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.PresupuestosDBConnection;
import views.budget.BudgetCreateView;
import views.budget.BudgetSearchView;
import views.budget.list.BudgetListView;
import views.budget.modify.BudgetModifyView;
import views.budget.cuttingService.CuttingServiceFormView;
import views.categories.CategoryCreateView;
import views.client.BudgetHistory.BudgetHistoryView;
import views.client.ClientCreateView;
import views.client.ClientSearchView;
import views.client.list.ClientListView;
import views.home.HomeView;
import views.home.IHomeView;
import views.products.ProductCreateView;
import views.products.ProductSearchView;
import views.products.list.ProductListView;
import views.settings.SettingsView;
import views.workbudget.WorkBudgetCreateView;
import views.workbudget.WorkBudgetSearchView;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        Locale.setDefault(Locale.US);

        PresupuestosDBConnection presupuestosDBConnection = new PresupuestosDBConnection();
        ClientesDBConnection  clientesDBConnection = new ClientesDBConnection();
        CategoriesDatabaseConnection categoriesDB = new CategoriesDatabaseConnection();
        categoriesDB.loadDatabase();
        ClientsDatabaseConnection clientsDB = new ClientsDatabaseConnection();
        clientsDB.loadDatabase();
        ProductsDatabaseConnection productsDB = new ProductsDatabaseConnection();
        productsDB.loadDatabase();
        BudgetsDatabaseConnection budgetsDB = new BudgetsDatabaseConnection();
        budgetsDB.loadDatabase();
        AttributesDatabaseConnection attributesDB = new AttributesDatabaseConnection();
        attributesDB.loadDatabase();
        SettingsDatabaseConnection settingsDB = new SettingsDatabaseConnection();
        settingsDB.loadDatabase();
        WorkBudgetsDatabaseConnection workBudgetsDB = new WorkBudgetsDatabaseConnection();
        workBudgetsDB.loadDatabase();
        WorkBudgetMaterialsDatabaseConnection workBudgetMaterialsDB = new WorkBudgetMaterialsDatabaseConnection();
        workBudgetMaterialsDB.loadDatabase();
        BudgetProductsDatabaseConnection budgetProductsDB = new BudgetProductsDatabaseConnection();
        budgetProductsDB.loadDatabase();

        IClientModel clientModel = new ClientModel(clientsDB, clientesDBConnection);
        IProductModel productModel = new ProductModel(productsDB, attributesDB, categoriesDB);
        IBudgetModel budgetModel = new BudgetModel(budgetsDB, presupuestosDBConnection, productsDB, clientsDB, clientesDBConnection);
        ICategoryModel categoryModel = new CategoryModel(categoriesDB);
        IProductListModel productListModel = new ProductListModel(productsDB);
        IClientListModel clientListModel = new ClientListModel(clientesDBConnection);
        IBudgetListModel budgetListModel = new BudgetListModel(budgetsDB);
        IBudgetModifyModel budgetModifyModel = new BudgetModifyModel(budgetsDB);
        ISettingsModel settingsModel = new SettingsModel(settingsDB);
        IBudgetHistoryModel budgetHistoryModel = new BudgetHistoryModel(budgetsDB, clientsDB);
        WorkBudgetModel workBudgetModel = new WorkBudgetModel(clientsDB, workBudgetsDB, clientesDBConnection);


        ClientCreateView clientCreateView = new ClientCreateView();
        ClientListView clientListView = new ClientListView();
        ProductCreateView productCreateView = new ProductCreateView();
        ProductListView productListView = new ProductListView();
        BudgetCreateView budgetCreateView = new BudgetCreateView();
        BudgetListView budgetListView = new BudgetListView();
        CuttingServiceFormView cuttingServiceFormView = new CuttingServiceFormView();
        CategoryCreateView categoryCreateView = new CategoryCreateView();
        BudgetModifyView budgetModifyView = new BudgetModifyView();
        SettingsView settingsView = new SettingsView();
        BudgetHistoryView budgetHistoryView = new BudgetHistoryView();

        ClientCreatePresenter clientCreatePresenter = new ClientCreatePresenter(clientCreateView, clientModel);
        ClientListPresenter clientListPresenter = new ClientListPresenter(clientListView, clientListModel);
        ProductCreatePresenter productCreatePresenter = new ProductCreatePresenter(productCreateView, productModel, categoryModel, settingsModel);
        ProductListPresenter productListPresenter = new ProductListPresenter(productListView, productListModel);
        CategoryCreatePresenter categoryCreatePresenter = new CategoryCreatePresenter(categoryCreateView);
        BudgetListPresenter budgetListPresenter = new BudgetListPresenter(budgetListView, budgetListModel);
        ProductSearchView productSearchView = new ProductSearchView(productListPresenter);
        ProductSearchPresenter productSearchPresenter = new ProductSearchPresenter(settingsModel, productSearchView, productModel, categoryModel);
        ClientSearchView clientSearchView = new ClientSearchView(clientListPresenter);
        BudgetCreatePresenter budgetCreatePresenter = new BudgetCreatePresenter(cuttingServiceFormView, budgetCreateView, budgetModel, productModel, categoryModel, settingsModel);
        BudgetModifyPresenter budgetModifyPresenter = new BudgetModifyPresenter(cuttingServiceFormView, budgetModifyView, budgetModel, productModel, categoryModel, budgetModifyModel, settingsModel);
        CuttingServiceFormPresenter cuttingServiceFormPresenter = new CuttingServiceFormPresenter(budgetModifyView, cuttingServiceFormView, budgetCreateView);
        BudgetHistoryPresenter budgetHistoryPresenter = new BudgetHistoryPresenter(budgetHistoryModel, budgetHistoryView, clientSearchView);
        IPresenterFactory presenterFactory = new PresenterFactoryImpl((BudgetHistoryModel) budgetHistoryModel, budgetHistoryView, clientSearchView);
        ClientSearchPresenter clientSearchPresenter = new ClientSearchPresenter(clientSearchView, clientModel, presenterFactory);
        BudgetSearchView budgetSearchView = new BudgetSearchView(budgetListPresenter, budgetModifyPresenter);
        BudgetSearchPresenter budgetSearchPresenter = new BudgetSearchPresenter(budgetSearchView, budgetCreateView, budgetModel, budgetModifyModel);
        SettingsPresenter settingsPresenter = new SettingsPresenter(settingsView, settingsModel);
        WorkBudgetCreateView workBudgetCreateView = new WorkBudgetCreateView();
        WorkBudgetCreatePresenter workBudgetCreatePresenter = new WorkBudgetCreatePresenter(workBudgetCreateView, workBudgetModel);
        WorkBudgetSearchView workBudgetSearchView = new WorkBudgetSearchView();
        WorkBudgetSearchPresenter workBudgetSearchPresenter = new WorkBudgetSearchPresenter(workBudgetSearchView, workBudgetCreateView, workBudgetModel);

        clientCreatePresenter.start();
        clientSearchPresenter.start();
        productCreatePresenter.start();
        productListPresenter.start();
        productSearchPresenter.start();
        budgetCreatePresenter.start();
        budgetSearchPresenter.start();
        cuttingServiceFormPresenter.start();
        categoryCreatePresenter.start();
        budgetListPresenter.start();
        budgetModifyPresenter.start();
        settingsPresenter.start();
        budgetHistoryPresenter.start();
        workBudgetCreatePresenter.start();
        workBudgetSearchPresenter.start();

        clientCreateView.start();
        clientSearchView.start();
        productCreateView.start();
        productSearchView.start();
        productListView.start();
        budgetCreateView.start();
        budgetSearchView.start();
        categoryCreateView.start();
        budgetListView.start();
        budgetModifyView.start();
        cuttingServiceFormView.start();
        settingsView.start();
        budgetHistoryView.start();
        workBudgetCreateView.start();
        workBudgetSearchView.start();

        IHomeView home = new HomeView(cuttingServiceFormPresenter, clientCreatePresenter, clientSearchPresenter, productSearchPresenter,
                productCreatePresenter, budgetSearchPresenter, budgetCreatePresenter, workBudgetCreatePresenter,
                workBudgetSearchPresenter, settingsPresenter, budgetHistoryPresenter);
    }

}