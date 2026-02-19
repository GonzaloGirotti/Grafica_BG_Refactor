package presenters.client;

import models.IClientModel;
import presenters.StandardPresenter;
import views.client.IClientCreateView;

import javax.swing.*;
import java.util.ArrayList;

import static utils.MessageTypes.*;

public class ClientCreatePresenter extends StandardPresenter {
    private final IClientCreateView clientCreateView;
    private final IClientModel clientModel;

    public ClientCreatePresenter(IClientCreateView clientCreateView, IClientModel clientModel) {
        this.clientCreateView = clientCreateView;
        view = clientCreateView;
        this.clientModel = clientModel;

    }

    public void initListeners() {
        clientModel.addClientCreationSuccessListener(() -> clientCreateView.showMessage(CLIENT_CREATION_SUCCESS));
        clientModel.addClientCreationFailureListener(() -> clientCreateView.showMessage(CLIENT_CREATION_FAILURE));
        clientModel.addCitiesFetchingSuccessListener(this::populateCityComboBox);
        clientModel.addClientCreationEmptyFieldListener(() -> clientCreateView.showMessage(ANY_CREATION_EMPTY_FIELDS));
    }

    private void populateCityComboBox() {
        clientCreateView.addCityToComboBox("Nueva localidad");
        for (String city : clientModel.getQueriedCities()) {
            clientCreateView.addCityToComboBox(city);
        }
    }

    public void onHomeCreateClientButtonClicked() {
        clientCreateView.showView();
    }



    public boolean onEmptyFields(JTextField nameField, JTextField cityField, JComboBox cityComboBox) {
        return nameField.getText().trim().isEmpty() || (cityField.getText().trim().isEmpty() && cityComboBox.getSelectedItem().equals("Nueva localidad"));
    }

        public void onCreateButtonClicked () {
            clientCreateView.setWorkingStatus();
            if (onEmptyFields(clientCreateView.getClientTextField(), clientCreateView.getCityTextField(), clientCreateView.getCityComboBox())) {
                clientCreateView.showMessage(ANY_CREATION_EMPTY_FIELDS);
            } else {
                String city = clientCreateView.getComboBoxSelectedCity().equals("Nueva localidad")
                        ? clientCreateView.getCityText()
                        : clientCreateView.getComboBoxSelectedCity();


                clientModel.createClient(
                        clientCreateView.getClientText(),
                        clientCreateView.getAddressText(),
                        city,
                        clientCreateView.getPhoneText(),
                        clientCreateView.isClientSelected()
                );


                clientCreateView.clearView();
            }
            clientCreateView.setWaitingStatus();
        }

        public void onCityComboBoxSelected () {
            boolean isNewCity = clientCreateView.getComboBoxSelectedCity().equals("Nueva localidad");
            clientCreateView.getCityTextField().setEnabled(isNewCity);
            if (!isNewCity) {
                clientCreateView.getCityTextField().setText("");
            }
        }
    }
