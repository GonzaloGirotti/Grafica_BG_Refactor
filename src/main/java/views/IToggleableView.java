package views;

import presenters.StandardPresenter;
import utils.MessageTypes;

public interface IToggleableView {
	void start();
	void showView();
	void hideView();
	void setWorkingStatus();
	void setWaitingStatus();
	void showMessage(MessageTypes messageType);
	void setPresenter(StandardPresenter standardPresenter);
}
