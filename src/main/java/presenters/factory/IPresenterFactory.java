package presenters.factory;

import presenters.client.BudgetHistoryPresenter;

public interface IPresenterFactory {
    BudgetHistoryPresenter createBudgetHistoryPresenter();
    void showBudgetHistoryView();
}
