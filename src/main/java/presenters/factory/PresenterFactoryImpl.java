package presenters.factory;

import models.BudgetHistoryModel;
import presenters.client.BudgetHistoryPresenter;
import views.client.BudgetHistory.BudgetHistoryView;
import views.client.ClientSearchView;

public class PresenterFactoryImpl implements IPresenterFactory {
    private final BudgetHistoryModel model;
    private final BudgetHistoryView view;
    private final ClientSearchView clientSearchView;

    public PresenterFactoryImpl(BudgetHistoryModel model, BudgetHistoryView view, ClientSearchView clientSearchView) {
        this.model = model;
        this.view = view;
        this.clientSearchView = clientSearchView;
    }

    @Override
    public BudgetHistoryPresenter createBudgetHistoryPresenter() {
        // La factory centraliza la creación
        return new BudgetHistoryPresenter(model, view, clientSearchView);
    }

    public void showBudgetHistoryView() {
        view.showView();
    }
}
